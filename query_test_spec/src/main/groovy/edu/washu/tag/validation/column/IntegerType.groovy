package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

class IntegerType extends ColumnType<String> {

    IntegerType(String columnName) {
        super(columnName)
    }

    IntegerType() {
        super()
    }

    @Override
    String readValue(Row row, int index) {
        row.isNullAt(index) ? null : String.valueOf(row.getInt(index))
    }

}
