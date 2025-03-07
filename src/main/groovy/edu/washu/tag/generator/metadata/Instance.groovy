package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.metadata.pixels.PixelSource
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.io.DicomOutputStream
import edu.washu.tag.generator.metadata.privateElements.PrivateBlock
import edu.washu.tag.generator.metadata.privateElements.PrivateElementContainer
import edu.washu.tag.generator.metadata.privateElements.StringPrivateElement

class Instance implements DicomEncoder, PrivateElementContainer {

    private static final List<Integer> IMAGE_PIXEL_TAGS = [Tag.Rows, Tag.Columns, Tag.PixelData, Tag.SamplesPerPixel, Tag.PhotometricInterpretation, Tag.BitsAllocated, Tag.BitsStored, Tag.HighBit, Tag.PixelRepresentation, Tag.WindowCenter, Tag.WindowWidth].sort()
    String sopClassUid
    String sopInstanceUid
    String instanceNumber
    PixelSource pixelSource
    List<PrivateBlock> privateBlocks = []

    void writeToDicomFile(Patient patient, Study study, Series series, File outputFile) {
        final Attributes attributes = new Attributes()

        attributes.setSpecificCharacterSet(study.characterSets as String[])
        patient.encode(attributes)
        study.encode(attributes)
        series.encode(attributes)
        encodePrivateElements(attributes)

        attributes.setString(Tag.SOPClassUID, VR.UI, sopClassUid)
        attributes.setString(Tag.SOPInstanceUID, VR.UI, sopInstanceUid)
        attributes.setString(Tag.InstanceNumber, VR.IS, instanceNumber)
        setIfNonempty(attributes, Tag.ImageType, VR.CS, series.imageType)

        String transferSyntaxUid = series.transferSyntaxUid
        if (pixelSource != null) {
            transferSyntaxUid = encodeImagePixelModule(study, series, attributes)
        }

        new DicomOutputStream(outputFile).withCloseable { dicomStream ->
            dicomStream.writeDataset(attributes.createFileMetaInformation(transferSyntaxUid), attributes)
        }
    }

    String encodeImagePixelModule(Study study, Series series, Attributes attributes) {
        final Attributes image = pixelSource.produceImage(study, series)
        attributes.addSelected(
            image,
            IMAGE_PIXEL_TAGS.toArray() as int[]
        )
        image.getString(Tag.TransferSyntaxUID)
    }

    @Override
    List<PrivateBlock> scopeAppropriatePrivateElements() {
        privateBlocks
    }

    @Override
    List<? extends PrivateElementContainer> childObjects() {
        null
    }

    @Override
    void recursePrivateElementResolution(Patient patient, List<List<PrivateBlock>> knownElements) {
        final PrivateBlock customPrivateElements = PrivateBlock.selfReferential()
        customPrivateElements.reserveByIndex(
                (knownElements.flatten() as List<PrivateBlock>).findAll { privateBlock ->
                    customPrivateElements.sameGroup(privateBlock)
                }
                        *.privateCreatorId
                        .unique()
                        .size()
        )
        privateBlocks << customPrivateElements
        knownElements << [customPrivateElements]
        customPrivateElements.elements << new StringPrivateElement(
                vr: VR.LT,
                elementNumber: '00',
                value: 'This data has been generated by a script. What appears to be PHI in this dataset is all simulated.'
        )
        customPrivateElements.elements << new StringPrivateElement(
                vr: VR.UI,
                elementNumber: '01',
                value: patient.patientInstanceUid,
                meaning: 'Patient Instance UID [nonstandard concept]'
        )

        final List<String> meaningsToWrap = []
        knownElements.each { elementList ->
            elementList.each { privateBlock ->
                privateBlock.elements.each { privateElement ->
                    if (privateElement.meaning) {
                        meaningsToWrap << "The private element encoded in ${privateElement.renderTag(privateBlock)} is intended to be: ${privateElement.meaning}".toString()
                    }
                }
            }
        }
        meaningsToWrap.each { meaning ->
            customPrivateElements.elements << new StringPrivateElement(
                    vr: VR.LT,
                    elementNumber: Integer.toString(customPrivateElements.elements.size(), 16), // simplifying assumption that will need to be revisited if the custom block starts being used more
                    value: meaning
            )
        }
    }

}
