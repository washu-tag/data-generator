package edu.washu.tag.generator

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.hl7.v2.ReportVersion

class CustomGeneratedReportGuarantee {

    Class<? extends GeneratedReport> reportClass
    List<ReportVersion> reportVersions

}
