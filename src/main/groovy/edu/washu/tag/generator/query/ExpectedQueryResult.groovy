package edu.washu.tag.generator.query

import edu.washu.tag.generator.BatchSpecification
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.Row

interface ExpectedQueryResult extends Serializable {

    void update(BatchSpecification batchSpecification)

    void validateResult(Dataset<Row> result)

}