package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.hl7.v2.CyclicVariedGptGenerator
import edu.washu.tag.generator.hl7.v2.ReportGenerator
import edu.washu.tag.generator.metadata.patient.EpicId
import edu.washu.tag.generator.metadata.patient.MainId
import edu.washu.tag.generator.metadata.patient.PatientId
import edu.washu.tag.generator.metadata.patient.PatientIdEncoder
import edu.washu.tag.generator.metadata.patient.SimplePatientIdEncoder
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.enums.Sex

import java.time.LocalDate
import java.time.LocalTime

class Patient implements DicomEncoder {

    private static final LocalDate imagingDataEpoch = LocalDate.of(2024, 6, 12) // patient might have been born in 1930, but we didn't have DICOM-compliant MRI machines in the 1930s!
    private static final List<PatientIdEncoder> patientIdEncoders = [
            new SimplePatientIdEncoder(MainId), new SimplePatientIdEncoder(EpicId)
    ]

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
    private static final ReportGenerator reportGenerator = new CyclicVariedGptGenerator() // TODO: how to allow other implementation?

    Patient randomize(SpecificationParameters specificationParameters, double currentAverageStudiesPerPatient, long previouslyGeneratedSeries, long previouslyGeneratedStudies) {
        long numSeries = previouslyGeneratedSeries
        long numStudies = previouslyGeneratedStudies
        patientIds = patientIdEncoders*.nextPatientId()
        final LocalDate sixteenthBirthday = dateOfBirth.plusYears(16)
        patientInstanceUid = UIDUtils.createUID()
        earliestAvailableStudyDate = sixteenthBirthday.isAfter(imagingDataEpoch) ? sixteenthBirthday : imagingDataEpoch
        specificationParameters.chooseNumberOfStudies(currentAverageStudiesPerPatient).times {
            final Protocol selectedProtocol = specificationParameters.chooseProtocol(numSeries == 0 ? 0.0 : numSeries / numStudies, this)
            final Study study = new Study(protocol : selectedProtocol, patient : this).randomize(specificationParameters, selectedProtocol)
            studies << study
            numStudies++
            numSeries += study.series.size()
        }
        this
    }

    void generateReports() {
        reportGenerator.generateReportsForPatient(this)
    }

    void encode(Attributes attributes) {
        attributes.setString(Tag.PatientSex, VR.CS, sex.dicomRepresentation)
        setDate(attributes, Tag.PatientBirthDate, dateOfBirth)
        setTime(attributes, Tag.PatientBirthTime, timeOfBirth)
        attributes.setString(Tag.PatientID, VR.LO, patientIds[0].idNumber)
    }

}
