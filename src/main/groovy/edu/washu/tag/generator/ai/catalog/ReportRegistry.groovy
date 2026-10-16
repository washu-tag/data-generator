package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.ai.GeneratedReport
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.RandomGenUtils

class ReportRegistry {

    static final List<Class<? extends GeneratedReport>> generableReportClasses = [
        SplitFindingsReport,
        SplitFindingsByModalityReport,
        CombinedFindingsImpressionReport,
        AbdominalAngiographyReport,
        AddendedReport,
        TechnicianNoteReport,
        DiagnosisReport,
        LiRadsUsSurveillance,
        LiRadsUs2024Surveillance,
        LiRadsCtMriDiagnostic,
        LiRadsCtMriNonradiationTra,
        LiRadsCtMriRadiationTra
    ]
    static final Map<Class<? extends GeneratedReport>, GeneratedReport> reportInstances = generableReportClasses.collectEntries { reportClass ->
        [(reportClass): reportClass.getDeclaredConstructor().newInstance()]
    }

    static Class<? extends GeneratedReport> lookupReportClass(String simpleName) {
        final Class<? extends GeneratedReport> reportClass = generableReportClasses.find { it.simpleName == simpleName }
        if (reportClass == null) {
            throw new IllegalArgumentException("Unknown report class '${simpleName}'. Known report classes: ${generableReportClasses*.simpleName}")
        }
        reportClass
    }

    static Class<? extends GeneratedReport> randomReportClass(Study study) {
        if (study.requestedReportClasses) {
            final List<Class<? extends GeneratedReport>> applicableRequested = study.requestedReportClasses.findAll { reportClass ->
                reportInstances[reportClass].checkApplicability(study)
            }
            if (applicableRequested.isEmpty()) {
                throw new IllegalStateException(
                    "None of the requested report classes ${study.requestedReportClasses*.simpleName} apply to a '${study.simpleDescription}' study"
                )
            }
            return RandomGenUtils.randomListEntry(applicableRequested)
        }

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
