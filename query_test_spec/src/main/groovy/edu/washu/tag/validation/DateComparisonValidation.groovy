package edu.washu.tag.validation

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.function.BiFunction

import static org.testng.AssertJUnit.assertTrue

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DateComparisonValidation implements LoggableValidation {

    String description
    String columnName
    Integer truncation
    Integer comparisonValue
    ComparisonOperator comparisonOperator
    private static final Logger logger = LoggerFactory.getLogger(DateComparisonValidation)

    @Override
    void log() {
        logger.info("Validating that each row satisfies: ${description}")
    }

    @Override
    ForeachFunction<Row> validation() {
        { Row row ->
            final String actualVal = row.getString(row.fieldIndex(columnName))
            final String transformed = truncation > 0 ? actualVal.substring(0, truncation) : actualVal
            assertTrue(comparisonOperator.compare.apply(Integer.parseInt(transformed), comparisonValue))
        }
    }

    static enum ComparisonOperator {
        EQ ({ a, b -> a == b }),
        NEQ ({ a, b -> a != b }),
        GT ({ a, b -> a > b }),
        GTE ({ a, b -> a >= b }),
        LT ({ a, b -> a < b }),
        LTE ({ a, b -> a <= b })

        BiFunction<Integer, Integer, Boolean> compare

        ComparisonOperator(BiFunction<Integer, Integer, Boolean> compare) {
            this.compare = compare
        }
    }

}
