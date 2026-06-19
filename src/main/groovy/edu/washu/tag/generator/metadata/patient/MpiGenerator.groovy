package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator
import edu.washu.tag.generator.metadata.Institutions
import edu.washu.tag.generator.metadata.Study

class MpiGenerator extends SimplePatientIdGenerator {

    MpiGenerator(int offset) {
        super(
            offset,
            { Study study ->
                final PatientId patientId = new PatientId(
                    checkApplicabilityFor: { it.radReport.hl7Version == ReportVersion.V2_4 }
                )
                switch (study.radReport.hl7Version) {
                    case ReportVersion.V2_4:
                        patientId.setAssigningAuthority(Institutions.getAssigningAuthorityForStudy(study))
                        patientId.setIdentifierTypeCode('EE')
                        break
                    case ReportVersion.V2_7:
                        patientId.setAssigningAuthority(HierarchicDesignator.simple('EMPI'))
                        patientId.setIdentifierTypeCode('MR')
                        break
                }
                patientId
            }
        )
    }

    @Override
    boolean conditionalIncludeIdOn(Study study) {
        study.radReport.hl7Version == ReportVersion.V2_7 && study.patient.studies.any {
            it.radReport.hl7Version == ReportVersion.V2_3
        }
    }
}
