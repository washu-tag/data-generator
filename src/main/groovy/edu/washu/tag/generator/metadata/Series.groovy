package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.privateElements.PrivateBlock
import edu.washu.tag.generator.metadata.privateElements.PrivateElementContainer

import java.time.LocalDate
import java.time.LocalTime

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
class Series implements DicomEncoder, PrivateElementContainer {

    String modality
    String seriesInstanceUid
    String seriesDescription
    String seriesNumber = ''
    Laterality laterality
    LocalDate seriesDate
    LocalTime seriesTime
    String protocolName
    List<String> imageType // technically this is an instance-level field, but it's reasonable to model it here
    List<String> operatorsName
    List<String> performingPhysiciansName
    BodyPart bodyPartExamined
    Equipment scanner
    String transferSyntaxUid
    List<PrivateBlock> seriesLevelPrivateBlocks
    List<Instance> instances

    // intermediate
    @JsonIgnore SeriesType seriesType
    @JsonIgnore int seriesIndex

    Series randomize(SpecificationParameters specificationParameters, SeriesType seriesType, Study study, int seriesIndex) {
        setScanner(study.equipmentMap.get(seriesType.seriesBundling()))
        setSeriesType(seriesType)
        setSeriesIndex(seriesIndex)
        seriesType.setSpecificationParameters(specificationParameters)
        setTransferSyntaxUid(scanner.getTransferSyntaxUID(seriesType))
        seriesType.allSeriesModules().each { seriesModule ->
            seriesModule.apply(specificationParameters, study.patient, study, scanner, this)
        }

        imageType = seriesType.getImageType(scanner)?.resolve()
        instances = seriesType.produceInstances(study.patient, study, scanner, this)
        this
    }

    void encode(Attributes attributes) {
        scanner.encode(attributes)

        attributes.setString(Tag.Modality, VR.CS, modality)
        attributes.setString(Tag.SeriesInstanceUID, VR.UI, seriesInstanceUid)
        attributes.setString(Tag.SeriesNumber, VR.IS, seriesNumber)
        setIfNonnull(attributes, Tag.Laterality, VR.CS, laterality?.dicomRepresentation)
        setDate(attributes, Tag.SeriesDate, seriesDate)
        setTime(attributes, Tag.SeriesTime, seriesTime)
        setIfNonnull(attributes, Tag.BodyPartExamined, VR.CS, bodyPartExamined?.dicomRepresentation)
        setIfNonnull(attributes, Tag.SeriesDescription, VR.LO, seriesDescription)
        setIfNonnull(attributes, Tag.ProtocolName, VR.LO, protocolName)
        setIfNonempty(attributes, Tag.OperatorsName, VR.PN, operatorsName)
        setIfNonempty(attributes, Tag.PerformingPhysicianName, VR.PN, performingPhysiciansName)
        encodePrivateElements(attributes)
    }

    @Override
    List<PrivateBlock> scopeAppropriatePrivateElements() {
        seriesLevelPrivateBlocks
    }

    @Override
    List<? extends PrivateElementContainer> childObjects() {
        instances
    }

}
