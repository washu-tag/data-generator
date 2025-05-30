package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

import java.time.LocalDate

class LocalDateType extends ColumnType<LocalDate> {

    LocalDateType(String columnName) {
        super(columnName)
    }

    LocalDateType() {
        super()
    }

    @Override
    LocalDate readValue(Row row, int index) {
        row.getLocalDate(index)
    }

}
