package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

import java.time.Instant

class IntegerType extends ColumnType<String> {

    IntegerType(String columnName) {
        super(columnName)
    }

    IntegerType() {
        super()
    }

    @Override
    String readValue(Row row, int index) {
        String.valueOf(row.getInt(index))
    }

}
