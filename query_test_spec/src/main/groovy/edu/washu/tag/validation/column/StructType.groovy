package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

class StructType extends ColumnType<String> {

    StructType(String columnName) {
        super(columnName)
    }

    StructType() {
        super()
    }

    @Override
    String readValue(Row row, int index) {
        row.getStruct(index).toString()
    }

}
