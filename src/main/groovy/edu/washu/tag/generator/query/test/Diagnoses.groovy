package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.ai.catalog.ClassicReport
import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.generator.query.FirstMatchingReportsRadReportResult
import edu.washu.tag.validation.column.*

import static edu.washu.tag.generator.hl7.v2.ReportVersion.*
import static edu.washu.tag.generator.query.QueryUtils.*

class Diagnoses extends TestQuery<BatchSpecification> {

    private static final String COLUMN_DIAGNOSES = 'diagnoses'
    private static final String COLUMN_DIAGNOSES_CONSOLIDATED = 'diagnoses_consolidated'

    Diagnoses() {
        super('diagnoses', null)
        CodeCache.initializeCache(1)
        withDataProcessor(
            new FirstMatchingReportsRadReportResult(
                1,
                classicReportWithContentAndVersion(V2_7).and({ (it.generatedReport as ClassicReport).parsedCodes.size() > 1 })
            ).withColumnExtractions({ RadiologyReport radiologyReport ->
                final ClassicReport classicReport = radiologyReport.generatedReport as ClassicReport
                [
                    (COLUMN_DIAGNOSES): serializeArrayOfStructByProperties(
                        classicReport.getParsedCodes(),
                        { diagnosisCode ->
                            [
                                diagnosisCode,
                                CodeCache.lookupCode(classicReport.designator, diagnosisCode),
                                classicReport.designator.hl7Representation
                            ]
                        }
                    ),
                    (COLUMN_DIAGNOSES_CONSOLIDATED): classicReport.getParsedCodes().collect { diagnosisCode ->
                        CodeCache.lookupCode(classicReport.designator, diagnosisCode)
                    }.join('; ')
                ]
            }).withColumnTypes([
                new ArrayType(COLUMN_DIAGNOSES)
            ] as Set<ColumnType<?>>)
        ).withPostProcessing({ query ->
            setSqlFindMessageControlIds(query, "${COLUMN_MESSAGE_CONTROL_ID}, ${COLUMN_DIAGNOSES}, ${COLUMN_DIAGNOSES_CONSOLIDATED}")
        })
    }

}
