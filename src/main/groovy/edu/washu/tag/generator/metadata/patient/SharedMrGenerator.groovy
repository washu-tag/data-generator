package edu.washu.tag.generator.metadata.patient

import edu.washu.tag.generator.hl7.v2.ReportVersion
import edu.washu.tag.generator.metadata.Institutions
import edu.washu.tag.generator.metadata.Study

class SharedMrGenerator extends SimplePatientIdGenerator {

    SharedMrGenerator(int offset) {
        super(
            offset,
            { Study study ->
                final PatientId patientId = new PatientId(
                    checkApplicabilityFor: { it.radReport.hl7Version == ReportVersion.V2_4 }
                )
                // Bakes in a relationship between these different AAs and the IDs. I think that's ok?
                patientId.setAssigningAuthority(Institutions.getAssigningAuthorityForStudy(study))
                patientId.setIdentifierTypeCode('MR')
                patientId
            }
        )
    }

}
