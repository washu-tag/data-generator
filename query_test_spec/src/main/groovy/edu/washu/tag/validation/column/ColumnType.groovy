package edu.washu.tag.validation.column

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.apache.spark.sql.Row

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
abstract class ColumnType<X> {

    String columnName

    ColumnType(String columnName) {
        this.columnName = columnName
    }

    abstract X readValue(Row row, int index)

}
