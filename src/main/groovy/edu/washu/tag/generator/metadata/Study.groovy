package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.privateElements.AsciiRepeatedBinaryPrivateElement
import edu.washu.tag.generator.metadata.privateElements.PrivateBlock
import edu.washu.tag.generator.metadata.privateElements.PrivateElementContainer

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class Study implements DicomEncoder, PrivateElementContainer {

    LocalDate studyDate
    LocalTime studyTime
    String studyInstanceUid
    String studyId = ''
    String studyDescription
    String accessionNumber = ''
    String patientAge = null
    Double patientSize = null
    Double patientWeight = null
    String patientName // Yes, this is a patient field, but we want the value to be inconsistently encoded *across studies*, but consistent within a study
    String referringPhysicianName = ''
    List<String> consultingPhysicianName
    List<String> characterSets = ['ISO_IR 100'] // simplifying assumption to put it here
    List<PrivateBlock> studyLevelPrivateBlocks = []
    List<Series> series = []
    RadiologyReport radReport
    String simpleDescription
    String procedureCodeId
    BodyPart bodyPartExamined // TODO: this is a simplifying assumption. This is really a series-level field
    @JsonIgnore Patient patient
    @JsonIgnore String ethnicGroup // Yes, this is a patient field, but we want the value to be inconsistently encoded *across studies*, but consistent within a study
    @JsonIgnore Protocol protocol
    @JsonIgnore Equipment primaryEquipment
    @JsonIgnore Map<SeriesBundling, Equipment> equipmentMap = [:]
    @JsonIgnore Map<SeriesBundling, Integer> dateOffsetMap = [:]
    @JsonIgnore Map<SeriesBundling, LocalTime> seriesTimeMap = [:]
    @JsonIgnore Map<Equipment, List<String>> operatorMap
    @JsonIgnore List<Person> primaryOperators
    @JsonIgnore List<String> performingPhysiciansName // Series level field, but in most cases it's going to be fixed across a study

    Study randomize(SpecificationParameters specificationParameters, Protocol protocol) {
        protocol.setFieldsFor(specificationParameters, patient, this)
        if (specificationParameters.includeBinariesInPrivateElements) {
            studyLevelPrivateBlocks << new PrivateBlock(
                    privateCreatorId: 'SYNTHETIC BINS',
                    groupNumber: '0015',
                    elements: [
                            new AsciiRepeatedBinaryPrivateElement(
                                    vr: VR.UN,
                                    elementNumber: '20',
                                    repetitionCount: new Random().nextInt(400000), // go up to ~4MB since we repeat a 10 byte string up to 400 000 times
                                    meaning: 'nonsense binary'
                            )
                    ]
            )
        }
        this
    }

    void encode(Attributes attributes) {
        setDate(attributes, Tag.StudyDate, studyDate)
        setTime(attributes, Tag.StudyTime, studyTime)
        attributes.setString(Tag.StudyInstanceUID, VR.UI, studyInstanceUid)
        attributes.setString(Tag.PatientName, VR.PN, patientName)
        attributes.setString(Tag.ReferringPhysicianName, VR.PN, referringPhysicianName)
        setIfNonnull(attributes, Tag.StudyID, VR.SH, studyId)
        setIfNonnull(attributes, Tag.StudyDescription, VR.LO, studyDescription)
        if (procedureCodeId != null) {
            attributes.newSequence(Tag.ProcedureCodeSequence, 1)
                << ProcedureCode.lookup(procedureCodeId).codedTriplet.toSequenceItem()
        }
        setIfNonnull(attributes, Tag.AccessionNumber, VR.SH, accessionNumber)
        setIfNonnull(attributes, Tag.EthnicGroup, VR.SH, ethnicGroup)
        setIfNonnull(attributes, Tag.PatientAge, VR.AS, patientAge)
        setIfNonnull(attributes, Tag.PatientSize, VR.DS, patientSize?.toString())
        setIfNonnull(attributes, Tag.PatientWeight, VR.DS, patientWeight?.toString())
        setIfNonempty(attributes, Tag.ConsultingPhysicianName, VR.PN, consultingPhysicianName)
        encodePrivateElements(attributes)
    }

    @Override
    List<PrivateBlock> scopeAppropriatePrivateElements() {
        studyLevelPrivateBlocks
    }

    @Override
    List<? extends PrivateElementContainer> childObjects() {
        series
    }

    @JsonIgnore
    LocalDateTime studyDateTime() {
        studyDate.atTime(studyTime)
    }

}
