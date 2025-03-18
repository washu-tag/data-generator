package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.GenerationContext
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.patient.PatientId
import edu.washu.tag.generator.util.SequentialIdGenerator
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.util.UIDUtils

import java.time.LocalDate
import java.time.LocalTime

class Patient implements DicomEncoder {

    private static final LocalDate imagingDataEpoch = LocalDate.of(1994, 6, 12) // patient might have been born in 1930, but we didn't have DICOM-compliant MRI machines in the 1930s!

    Sex sex
    LocalDate dateOfBirth
    LocalTime timeOfBirth
    Person patientName
    List<PatientId> patientIds
    Race race
    List<Study> studies = []
    String patientInstanceUid // non-standard element
    @JsonIgnore LocalDate earliestAvailableStudyDate
    @JsonIgnore Nationality nationality
    @JsonIgnore double personalHeightMod
    @JsonIgnore double personalWeightMod

    Patient randomize(GenerationContext generationContext, SequentialIdGenerator studyIdGenerator) {
        final LocalDate sixteenthBirthday = dateOfBirth.plusYears(16)
        patientInstanceUid = UIDUtils.createUID()
        patientIds = generationContext.patientIdEncoders*.nextPatientId()
        earliestAvailableStudyDate = sixteenthBirthday.isAfter(imagingDataEpoch) ? sixteenthBirthday : imagingDataEpoch
        generationContext.calculateStudyCountForCurrentPatient().times {
            final Protocol selectedProtocol = generationContext.chooseProtocol(this)
            final Study study = new Study(protocol : selectedProtocol, patient : this)
                .randomize(generationContext.specificationParameters, studyIdGenerator, selectedProtocol)
            studies << study
            generationContext.previouslyGeneratedStudies++
            generationContext.previouslyGeneratedSeries += study.series.size()
        }
        this
    }

    void encode(Attributes attributes) {
        attributes.setString(Tag.PatientSex, VR.CS, sex.dicomRepresentation)
        setDate(attributes, Tag.PatientBirthDate, dateOfBirth)
        setTime(attributes, Tag.PatientBirthTime, timeOfBirth)
        attributes.setString(Tag.PatientID, VR.LO, patientIds[0].idNumber)
    }

}
