package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Study

import java.util.concurrent.ThreadLocalRandom

class LegacyIdGenerator extends SimplePatientIdGenerator {

    public static final HierarchicDesignator facility = HierarchicDesignator.simple('UN')

    LegacyIdGenerator(int offset) {
        super(
            offset,
            { Study study ->
                final PatientId patientId = new PatientId(
                    checkApplicabilityFor: { it.radReport.hl7Version == ReportVersion.V2_3 }
                )
                patientId.setIdentifierCheckDigit(String.valueOf(ThreadLocalRandom.current().nextInt(1, 10)))
                patientId.setCheckDigitScheme(String.valueOf(ThreadLocalRandom.current().nextInt(1, 10)))
                patientId.setAssigningFacility(facility)
                patientId
            }
        )
    }

}
