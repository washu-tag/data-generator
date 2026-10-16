package edu.washu.tag.generator

import edu.washu.tag.generator.hl7.v2.FixedSampleReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.patient.DefaultPatientRandomizer
import edu.washu.tag.generator.metadata.patient.GreekPatientRandomizer
import edu.washu.tag.generator.metadata.patient.JapanesePatientRandomizer
import edu.washu.tag.generator.metadata.patient.KoreanPatientRandomizer
import edu.washu.tag.generator.metadata.patient.PatientRandomizer
import edu.washu.tag.generator.metadata.study.StudyDateDistribution
import edu.washu.tag.generator.metadata.study.UniformStudyDateDistribution
import edu.washu.tag.generator.util.RandomGenUtils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SpecificationParameters {

    int numPatients
    int numStudies
    int numSeries
    List<Protocol> protocols
    List<Protocol> protocolsBelowNumSeriesExpectedAverage = []
    List<Protocol> protocolsAboveNumSeriesExpectedAverage = []
    double averageStudiesPerPatient
    double averageSeriesPerStudy
    boolean requireXnatCompatibility
    boolean createFullSopInstances = true
    int patientAgeEncodingPercent = 100
    int patientSizeEncodingPercent = 100
    int patientSizeEncodedAsZeroPercent = 5
    int patientWeightEncodingPercent = 100
    int patientWeightEncodedAsZeroPercent = 5
    boolean includePixelData
    boolean includeBinariesInPrivateElements
    boolean generateRadiologyReports = false
    StudyDateDistribution studyDateDistribution = new UniformStudyDateDistribution()
    ReportGenerator reportGeneratorImplementation = new FixedSampleReportGenerator()
    List<CustomGeneratedReportGuarantee> customReportGuarantees = [] // only for generating scout tests at small scale
    List<PatientRandomizer> patientRandomizers = [
        (new DefaultPatientRandomizer(randomizerWeight: 85)),
        (new GreekPatientRandomizer(randomizerWeight: 5)),
        (new JapanesePatientRandomizer(randomizerWeight: 5)),
        (new KoreanPatientRandomizer(randomizerWeight: 5))
    ]
    List<Institution> institutionOverrides = []
    List<SpecializedCohort> cohorts = []

    private void validateReportSelection() {
        if (!generateRadiologyReports) {
            return
        }
        final List<String> cohortsRequestingClasses = cohorts.findAll { it.requestsReportClasses() }*.name
        if (cohortsRequestingClasses && !reportGeneratorImplementation.honorsRequestedReportClasses()) {
            throw new IllegalArgumentException(
                "Cohorts ${cohortsRequestingClasses} request reportClasses, but the report generator would ignore them. " +
                    'Use the gpt report generator with customReportFraction set (any value; cohort patients always get customized reports).'
            )
        }
        customReportGuarantees.each { guarantee ->
            final List<ReportVersion> supportedVersions = guarantee.reportClass.getDeclaredConstructor().newInstance().supportedVersions()
            final List<ReportVersion> unsupported = guarantee.reportVersions - supportedVersions
            if (unsupported) {
                throw new IllegalArgumentException(
                    "Report guarantee for ${guarantee.reportClass.simpleName} requests HL7 versions ${unsupported}, but it only supports ${supportedVersions}"
                )
            }
        }
    }

    void postprocess() {
        validateReportSelection()
        cohorts.each { cohort ->
            cohort.validateDiagnosisCodes()
            cohort.validateReportClasses()
            cohort.trajectory.each { studyRequest ->
                studyRequest.protocol.postprocess(this)
            }
        }
        
        if (numPatients == 0) {
            return
        }
        averageStudiesPerPatient = numStudies / numPatients
        averageSeriesPerStudy = (numSeries as double) / numStudies

        if (numStudies > numSeries) {
            throw new RuntimeException('Cannot request more studies than series.')
        }

        if (averageStudiesPerPatient < 3) {
            throw new RuntimeException('Average studies per patient must be at least 3.')
        }

        protocols.each { protocol ->
            if (protocol.isXnatCompatible() || !requireXnatCompatibility) {
                protocol.postprocess(this)
                if (protocol.seriesTypes.size() >= averageSeriesPerStudy) {
                    protocolsAboveNumSeriesExpectedAverage << protocol
                } else {
                    protocolsBelowNumSeriesExpectedAverage << protocol
                }
            } else {
                println("One of the protocols you requested (class name: ${protocol.class.simpleName}) is not compatible with XNAT. Because you are requiring XNAT compatibility, studies will not be generated under this protocol.")
            }
        }

        if (protocolsAboveNumSeriesExpectedAverage.isEmpty() || protocolsBelowNumSeriesExpectedAverage.isEmpty()) {
            final String userRequestDefinition = "You requested ${numSeries} series with ${numStudies} studies, which averages to ${averageSeriesPerStudy} series per study."

            if (protocolsAboveNumSeriesExpectedAverage.isEmpty()) {
                throw new RuntimeException("${userRequestDefinition} However, none of the protocols you specified have that many series.")
            }
            throw new RuntimeException("${userRequestDefinition} However, none of the protocols you specified have that few series.")
        }
    }

    int chooseNumberOfStudies(double currentCumulativeAverageStudiesPerPatient) {
        if (currentCumulativeAverageStudiesPerPatient >= averageStudiesPerPatient) {
            RandomGenUtils.randomListEntry((1 .. Math.floor(averageStudiesPerPatient).intValue()).toList())
        } else {
            final int min = Math.ceil(averageStudiesPerPatient).intValue()
            RandomGenUtils.setupWeightedLottery([
                    (min) : 100,
                    (min + 1) : 80,
                    (min + 2) : 55,
                    (min + 3) : 30,
                    (min + 4) : 5
            ]).sample()
        }
    }

    Protocol chooseProtocol(double currentAverageSeriesPerStudy, Patient patient) {
        final List<Protocol> desiredList = currentAverageSeriesPerStudy >= averageSeriesPerStudy ? protocolsBelowNumSeriesExpectedAverage : protocolsAboveNumSeriesExpectedAverage
        final List<Protocol> backupList = ([protocolsBelowNumSeriesExpectedAverage, protocolsAboveNumSeriesExpectedAverage] - [desiredList])[0]
        final List<Protocol> filteredList = desiredList.findAll { protocol ->
            protocol.isApplicableFor(patient)
        }
        if (filteredList.isEmpty()) {
            RandomGenUtils.randomListEntry(backupList.findAll { protocol ->
                protocol.isApplicableFor(patient)
            })
        } else {
            RandomGenUtils.randomListEntry(filteredList)
        }
    }

}
