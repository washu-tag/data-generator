package edu.washu.tag.validation

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

@JsonTypeInfo(
    use = JsonTypeInfo.Id.MINIMAL_CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = 'type'
)
interface ExpectedQueryResult extends Serializable {

    void validateResult(Dataset<Row> result, TestContext testContext)

}