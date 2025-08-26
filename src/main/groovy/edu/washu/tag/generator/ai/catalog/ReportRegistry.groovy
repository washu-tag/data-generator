package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.RandomGenUtils

class ReportRegistry {

    static final List<Class<? extends GeneratedReport>> generableReportClasses = [
        SplitFindingsReport,
        SplitFindingsByModalityReport,
        CombinedFindingsImpressionReport
    ]
    static final Map<Class<? extends GeneratedReport>, GeneratedReport> reportInstances = generableReportClasses.collectEntries { reportClass ->
        [(reportClass): reportClass.getDeclaredConstructor().newInstance()]
    }

    static Class<? extends GeneratedReport> randomReportClass(Study study) {
        final Set<Class<? extends GeneratedReport>> permissibleReportClasses = reportInstances.findAll { entry ->
            entry.value.checkApplicability(study)
        }.keySet()

        if (permissibleReportClasses.isEmpty()) {
            ClassicReport
        } else {
            RandomGenUtils.randomListEntry(permissibleReportClasses.toList())
        }
    }

}
