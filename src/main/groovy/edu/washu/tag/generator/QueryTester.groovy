package edu.washu.tag.generator

import io.delta.sql.DeltaSparkSessionExtension
import org.apache.spark.sql.SparkSession

class QueryTester {

    static void main(String[] args) {
        final SparkSession spark = SparkSession.builder()
            .appName('TestClient')
            .withExtensions({ new DeltaSparkSessionExtension() })
            .config('spark.sql.catalog.spark_catalog', 'org.apache.spark.sql.delta.catalog.DeltaCatalog')
            .config('spark.hadoop.fs.s3a.access.key', 'admin')
            .config("spark.hadoop.fs.s3a.secret.key", "password") \
            .config("spark.hadoop.fs.s3a.endpoint", "http://10.27.107.3:9000") \
            .config("spark.hadoop.fs.s3a.endpoint.region", "us-east-1") \
            .config('spark.hadoop.fs.s3a.aws.credentials.provider', 'org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider') \
            .getOrCreate()

        spark
            .read()
            .format('delta')
            .load('s3a://synthetic/delta/hl7_v3')
            .createOrReplaceTempView('syntheticdata')

        spark.sql("SELECT * FROM syntheticdata WHERE pid_8_administrative_sex='F'").count()
    }

}
