package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.patient.*
import edu.washu.tag.generator.query.FirstPatientsRadReportResult
import edu.washu.tag.generator.query.QueryUtils
import edu.washu.tag.validation.column.ArrayType
import edu.washu.tag.validation.column.ColumnType

import static edu.washu.tag.generator.query.QueryUtils.COLUMN_PATIENT_IDS
import static edu.washu.tag.generator.query.QueryUtils.TABLE_NAME

class PatientIdQuery extends TestQuery<BatchSpecification> {

    PatientIdQuery() {
        super('patient_id', null)
        withDataProcessor(
            new FirstPatientsRadReportResult(5)
                .withColumnExtractions({ radiologyReport ->
                    final Map<String, String> patientIds = [new MainId(), new EpicId(), new LegacyId(), new EmpiId()]*.expectedColumnName().collectEntries { columnName ->
                        [(columnName): radiologyReport.patientIds.find { it.expectedColumnName() == columnName }?.idNumber]
                    }
                    patientIds.put(
                        (COLUMN_PATIENT_IDS),
                        QueryUtils.serializeArrayOfStructByProperties(
                            radiologyReport.patientIds,
                            { patientId ->
                                [
                                    patientId.idNumber,
                                    patientId.assigningAuthority?.getNamespaceId(),
                                    patientId.identifierTypeCode,
                                    patientId.assigningFacility?.getNamespaceId()
                                ]
                            }
                        )
                    )
                    patientIds
                }).withColumnTypes([
                    new ArrayType(COLUMN_PATIENT_IDS)
                ] as Set<ColumnType<?>>)
        ).withPostProcessing({ query ->
            final Map<String, Set<String>> matchingIds = [MainId, LegacyId].collectEntries { Class<PatientId> idClass ->
                [(idClass.getDeclaredConstructor().newInstance().expectedColumnName()): []]
            }
            (query.querySourceDataProcessor as FirstPatientsRadReportResult).expectation.rowAssertions.each { row ->
                row.value.each { column, id ->
                    if (column in matchingIds.keySet() && id != null) {
                        matchingIds[column] << id
                    }
                }
            }
            final String whereClause = matchingIds.collect { idGrouping ->
                "${idGrouping.key} IN (${idGrouping.value.collect { "'${it}'" }.join(', ')})"
            }.join(' OR ')
            query.setSql("SELECT * FROM ${TABLE_NAME} WHERE (${whereClause})")
        })
    }

}
