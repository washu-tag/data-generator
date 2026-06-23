package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.generator.GenerationContext
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.cohorting.StudyRequest
import edu.washu.tag.generator.metadata.enums.Nationality
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.metadata.patient.EpicMrnGenerator
import edu.washu.tag.generator.metadata.patient.MpiGenerator
import edu.washu.tag.generator.metadata.patient.PatientIdGenerator
import edu.washu.tag.generator.metadata.study.StudyDateDistribution
import edu.washu.tag.generator.metadata.study.UniformStudyDateDistribution
import edu.washu.tag.generator.util.SequentialIdGenerator
import org.apache.commons.lang3.RandomUtils
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import org.dcm4che3.util.UIDUtils

import java.time.LocalDate
import java.time.LocalTime

class Patient implements DicomEncoder {

    public static final LocalDate imagingDataEpoch = LocalDate.of(1994, 6, 12) // patient might have been born in 1930, but we didn't have DICOM-compliant MRI machines in the 1930s!

    Sex sex
    LocalDate dateOfBirth
    LocalTime timeOfBirth
    Person patientName
    Race race
    List<Study> studies = []
    String patientInstanceUid // non-standard element
    String mpi
    String epicMrn
    @JsonIgnore Map<PatientIdGenerator, String> patientIds
    @JsonIgnore LocalDate earliestAvailableStudyDate
    @JsonIgnore Nationality nationality
    @JsonIgnore double personalHeightMod
    @JsonIgnore double personalWeightMod
    @JsonIgnore boolean reportsMatched = false
    @JsonIgnore boolean compareAdjacentStudies = false
    @JsonIgnore SpecializedCohort parentCohort

    Patient randomize(GenerationContext generationContext, SequentialIdGenerator studyIdGenerator, SpecializedCohort parentCohort = null) {
        final LocalDate sixteenthBirthday = dateOfBirth.plusYears(16)
        patientInstanceUid = UIDUtils.createUID()
        patientIds = generationContext.patientIdGenerators.collectEntries { idGen ->
            final String id = idGen.nextPatientId()
            if (idGen instanceof EpicMrnGenerator) {
                epicMrn = id
            } else if (idGen instanceof MpiGenerator) {
                mpi = id
            }
            [(idGen): id]
        }
        earliestAvailableStudyDate = sixteenthBirthday.isAfter(imagingDataEpoch) ? sixteenthBirthday : imagingDataEpoch
        if (parentCohort == null) {
            generationContext.calculateStudyCountForCurrentPatient().times {
                final Study study = new Study(protocol : generationContext.chooseProtocol(this), patient : this)
                    .randomize(generationContext.specificationParameters, studyIdGenerator)
                studies << study
                generationContext.previouslyGeneratedStudies++
                generationContext.previouslyGeneratedSeries += study.series.size()
            }
        } else {
            this.parentCohort = parentCohort
            compareAdjacentStudies = true
            int trajectoryDuration = 0
            final Map<StudyRequest, Integer> offsetMapForCohort = parentCohort.trajectory.collectEntries { studyReq ->
                if (studyReq != parentCohort.trajectory[0]) {
                    final int studyOffset = RandomUtils.insecure().randomInt(studyReq.studyOffsetMin, studyReq.studyOffsetMax + 1)
                    trajectoryDuration += studyOffset
                    [(studyReq): trajectoryDuration]
                } else {
                    [(studyReq): 0]
                }
            }
            final StudyDateDistribution defaultDistribution = generationContext.specificationParameters.studyDateDistribution
            final LocalDate latestPossibleStartDate = defaultDistribution.maxDate.minusDays(trajectoryDuration)
            final LocalDate startDate = new UniformStudyDateDistribution(
                minDate: defaultDistribution.minDate,
                maxDate: latestPossibleStartDate
            ).generateStudyDate(this)

            parentCohort.trajectory.each { studyRequest ->
                final Study study = new Study(
                    protocol: studyRequest.protocol,
                    patient : this,
                    studyDate: startDate.plusDays(offsetMapForCohort.get(studyRequest))
                ).randomize(generationContext.specificationParameters, studyIdGenerator)
                studies << study
                generationContext.previouslyGeneratedStudies++
                generationContext.previouslyGeneratedSeries += study.series.size()
            }
        }

        this
    }

    void encode(Study study, Attributes attributes) {
        attributes.setString(Tag.PatientSex, VR.CS, sex.dicomRepresentation)
        setDate(attributes, Tag.PatientBirthDate, dateOfBirth)
        setTime(attributes, Tag.PatientBirthTime, timeOfBirth)
        attributes.setString(Tag.PatientID, VR.LO, study.patientIds[0].idNumber)
    }

}
