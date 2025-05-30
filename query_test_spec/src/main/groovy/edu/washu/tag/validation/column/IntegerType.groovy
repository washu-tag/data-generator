package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

import java.time.Instant

class IntegerType extends ColumnType<Integer> {

    IntegerType(String columnName) {
        super(columnName)
    }

    IntegerType() {
        super()
    }

    @Override
    Integer readValue(Row row, int index) {
        row.getInt(index)
    }

}
