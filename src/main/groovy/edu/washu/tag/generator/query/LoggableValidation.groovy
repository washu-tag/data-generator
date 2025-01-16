package edu.washu.tag.generator.query

import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row

interface LoggableValidation extends Serializable {

    void log()

    ForeachFunction<Row> validation()

}
