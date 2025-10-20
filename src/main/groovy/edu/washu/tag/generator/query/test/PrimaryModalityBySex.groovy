package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.ProcedureCode
import edu.washu.tag.generator.metadata.enums.Sex
import edu.washu.tag.generator.query.GroupedAggregationRadReportResult
import edu.washu.tag.util.FileIOUtils

import static edu.washu.tag.generator.query.QueryUtils.sexFilter

class PrimaryModalityBySex extends TestQuery<BatchSpecification> {

    PrimaryModalityBySex() {
        super('modality_grouping', FileIOUtils.readResource('modality_query.sql'))
        withDataProcessor(
            new GroupedAggregationRadReportResult({ true })
                .primaryColumn('primary_modality')
                .primaryColumnDerivation({ report ->
                    ProcedureCode.lookup(report.study.procedureCodeId).impliedModality
                }).addCase(
                    new GroupedAggregationRadReportResult.Case(
                        'male_count',
                        sexFilter(Sex.MALE)
                    )
                ).addCase(
                    new GroupedAggregationRadReportResult.Case(
                        'female_count',
                        sexFilter(Sex.FEMALE)
                    )
                ).addCase(
                    new GroupedAggregationRadReportResult.Case(
                        'total_count',
                        { true }
                    )
                )
        )
    }

}
