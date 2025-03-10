package edu.washu.tag.generator

import edu.washu.tag.generator.hl7.v2.FixedSampleReportGenerator
import edu.washu.tag.generator.hl7.v2.ReportGenerator
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.util.RandomGenUtils

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
    ReportGenerator reportGeneratorImplementation = new FixedSampleReportGenerator()

    void postprocess() {
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

    /* boolean normalizeSeriesDescription
    boolean normalizePatientNames
    boolean includeOutsideHospitals
    boolean includeInfants*/


}
