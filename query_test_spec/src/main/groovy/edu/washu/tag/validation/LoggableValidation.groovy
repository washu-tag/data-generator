package edu.washu.tag.validation

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
interface LoggableValidation extends Serializable {

    void log()

    ForeachFunction<Row> validation()

}
