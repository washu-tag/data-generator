package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Institution
import edu.washu.tag.generator.metadata.NameCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.Race
import edu.washu.tag.generator.metadata.institutions.ChestertonAdamsHospital
import edu.washu.tag.generator.metadata.patient.PatientId
import edu.washu.tag.generator.metadata.reports.RadiologyReport2_7
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator
import org.apache.commons.lang3.RandomUtils
import org.dcm4che3.util.UIDUtils

import java.util.function.Supplier

class CurrentStudyReportGenerator extends StudyReportGenerator {

    private Supplier<String> visitIdGenerator = new SequentialIdGenerator(8).prefix('V')

    @Override
    RadiologyReport generateReportFrom(Patient patient, Study study, MessageRequirements messageRequirements, GeneratedReport generatedReport) {
        final Institution procedureInstitution = study.primaryEquipment.institution ?: new ChestertonAdamsHospital()
        final RadiologyReport radReport = initReport()
        study.setRadReport(radReport)
        radReport.setReportDateTime(
            study.studyDate.atTime(study.studyTime).plusSeconds(RandomUtils.insecure().randomInt(900, 10800))
        )
        radReport.setGeneratedReport(generatedReport)
        radReport.setStudy(study)
        radReport.setMessageControlId(UIDUtils.createUID())
        radReport.setPatientIds(patient.patientIdsForStudy(study))
        radReport.setIncludeAlias(messageRequirements.includePatientAlias)
        radReport.setRace(messageRequirements.isRaceUnavailable() ? unavailableRace() : patient.getRace())
        radReport.setSpecifyAddress(messageRequirements.specifyAddress)
        radReport.setExtendedPid(messageRequirements.extendedPid)
        radReport.setMalformInterpretersTechnician(messageRequirements.malformObrInterpretersAndTech)
        radReport.setIncludeObx(messageRequirements.includeObx)
        chooseInterpreters(radReport, procedureInstitution, messageRequirements)
        radReport.setAttendingDoctors(NameCache.selectPhysicians(procedureInstitution, messageRequirements.numAttendingDoctors))
        if (study.primaryOperators != null) {
            radReport.setTechnician(study.primaryOperators[0])
        }
        radReport.setVisitNumber(visitIdGenerator.get())
        radReport.setOrderingProvider(NameCache.selectPhysician(procedureInstitution))
        radReport.setOrcStatus(messageRequirements.orcStatus)
        radReport.setReasonForStudy(messageRequirements.reasonForStudy)
        radReport.setTransportationMode(messageRequirements.transportationMode)
        setOrderPlacerNumber(radReport)

        radReport
    }

    @Override
    RadiologyReport initReport() {
        new RadiologyReport2_7()
    }

    @Override
    Boolean checkReportCompatibility(GeneratedReport generatedReport) {
        generatedReport.supportedVersions().contains(initReport().hl7Version)
    }

    protected Race unavailableRace() {
        RandomUtils.insecure().randomBoolean() ? Race.UNABLE_TO_PROVIDE : Race.DECLINED_TO_PROVIDE
    }

    // TODO: this isn't really accurate
    protected void chooseInterpreters(RadiologyReport radReport, Institution institution, MessageRequirements messageRequirements) {
        radReport.setAssistantInterpreters(
                NameCache.selectPhysicians(
                        institution,
                        messageRequirements.numAsstInterpreters
                )
        )
    }

    protected void chooseInterpretersWithPrimary(RadiologyReport radReport, Institution institution, MessageRequirements messageRequirements) {
        final List<Person> allInterpreters = NameCache.selectPhysicians(
            institution,
            messageRequirements.numAsstInterpreters + 1
        )
        radReport.setPrincipalInterpreter(allInterpreters[0])
        radReport.setAssistantInterpreters(allInterpreters[1 .. -1])
    }

    protected void setOrderPlacerNumber(RadiologyReport radReport) {
        radReport.setPlacerOrderNumberId(RandomGenUtils.randomIdStr())
        radReport.setPlacerOrderNumberNamespace('SYS')
    }

}
