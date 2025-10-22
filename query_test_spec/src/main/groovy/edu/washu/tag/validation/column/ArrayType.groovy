package edu.washu.tag.validation.column

import org.apache.spark.sql.Row

class ArrayType extends ColumnType<String> {

    ArrayType(String columnName) {
        super(columnName)
    }

    ArrayType() {
        super()
    }

    @Override
    String readValue(Row row, int index) {
        '[' + row.getList(index).collect { it.toString() }.join(', ') + ']'
    }

}
