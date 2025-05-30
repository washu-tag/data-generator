package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

import java.time.Instant

class InstantType extends ColumnType<Instant> {

    InstantType(String columnName) {
        super(columnName)
    }

    InstantType() {
        super()
    }

    @Override
    Instant readValue(Row row, int index) {
        row.getInstant(index)
    }

}
