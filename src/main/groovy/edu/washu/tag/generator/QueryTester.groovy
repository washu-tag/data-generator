package edu.washu.tag.generator

import com.fasterxml.jackson.databind.ObjectMapper
import edu.washu.tag.generator.query.QueryGenerator
import io.delta.sql.DeltaSparkSessionExtension
import org.apache.spark.sql.SparkSession

class QueryTester {

    static void main(String[] args) {
        final SparkSession spark = SparkSession.builder()
            .appName('TestClient')
            .master('local')
            .withExtensions({ new DeltaSparkSessionExtension() })
            .config('spark.sql.catalog.spark_catalog', 'org.apache.spark.sql.delta.catalog.DeltaCatalog')
            .config('spark.hadoop.fs.s3a.access.key', 'admin')
            .config('spark.hadoop.fs.s3a.secret.key', 'password')
            .config('spark.hadoop.fs.s3a.endpoint', 'http://10.27.107.3:9000')
            .config('spark.hadoop.fs.s3a.endpoint.region', 'us-east-1')
            .getOrCreate()

        spark
            .read()
            .format('delta')
            .load('s3a://synthetic/delta/hl7_v3')
            .createOrReplaceTempView('syntheticdata')

        final BatchSpecification batchSpecification = new YamlObjectMapper().readValue(new File('batches/batch_0.yaml'), BatchSpecification)
        final QueryGenerator queryGenerator = new QueryGenerator()
        queryGenerator.processData(batchSpecification)
        queryGenerator.getTestQueries().each { testQuery ->
            testQuery.expectedQueryResult.validateResult(
                spark.sql(testQuery.sql)
            )
        }
    }

}
