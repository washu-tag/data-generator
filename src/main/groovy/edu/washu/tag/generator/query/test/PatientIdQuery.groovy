package edu.washu.tag.generator.query.test

import edu.washu.tag.TestQuery
import edu.washu.tag.generator.BatchSpecification
import edu.washu.tag.generator.metadata.patient.EmpiId
import edu.washu.tag.generator.metadata.patient.EpicId
import edu.washu.tag.generator.metadata.patient.LegacyId
import edu.washu.tag.generator.metadata.patient.MainId
import edu.washu.tag.generator.metadata.patient.PatientId
import edu.washu.tag.generator.query.FirstPatientsRadReportResult

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
                        'patient_ids',
                        '[' + radiologyReport.patientIds.collect { patientId ->
                            "{${patientId.idNumber}, ${patientId.assigningAuthority?.getNamespaceId() ?: ''}, ${patientId.identifierTypeCode ?: ''}, ${patientId.assigningFacility?.getNamespaceId() ?: ''}}"
                        }.join(', ') + ']'
                    )
                    patientIds
                })
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
