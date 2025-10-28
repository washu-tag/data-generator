package edu.washu.tag.generator.query

import edu.washu.tag.generator.metadata.RadiologyReport
import edu.washu.tag.validation.column.ColumnType

import java.util.function.Function

trait WithColumnExtractions<T extends WithColumnExtractions<T>> {

    Function<RadiologyReport, Map<String, String>> columnExtractions = { [:] }
    Set<ColumnType<?>> columnTypes = []

    T withColumnExtractions(Function<RadiologyReport, Map<String, String>> columnExtractions) {
        this.columnExtractions = columnExtractions
        this as T
    }

    T withColumnTypes(Set<ColumnType<?>> columnTypes) {
        this.columnTypes = columnTypes
        this as T
    }

}