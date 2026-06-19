package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Institutions
import edu.washu.tag.generator.metadata.Study

class EpicMrnGenerator extends SimplePatientIdGenerator {

    private static final HierarchicDesignator assigningAuthority = HierarchicDesignator.simple('EPIC')

    EpicMrnGenerator(int offset) {
        super(
            offset,
            { Study study ->
                final PatientId patientId = new PatientId(
                    checkApplicabilityFor: { it.radReport.hl7Version == ReportVersion.V2_7 }
                )
                if (study.primaryEquipment.institution == Institutions.southGrandMammographySpecialty) {
                    patientId.setAssigningAuthority(Institutions.southGrandMammographySpecialty.assigningAuthority)
                    patientId.setIdentifierTypeCode('MR')
                } else {
                    patientId.setAssigningAuthority(assigningAuthority)
                    patientId.setIdentifierTypeCode('MRN')
                }
                patientId
            }
        )
    }

    @Override
    boolean isDefault() {
        true
    }

}
