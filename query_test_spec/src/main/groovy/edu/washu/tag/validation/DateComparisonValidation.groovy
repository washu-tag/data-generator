package edu.washu.tag.validation

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.apache.spark.api.java.function.ForeachFunction
import org.apache.spark.sql.Row
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static org.assertj.core.api.Assertions.assertThat

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class DateComparisonValidation implements LoggableValidation {

    String description
    String columnName
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
            comparisonOperator.compare(
                row.getDate(row.fieldIndex(columnName)).toLocalDate(),
                parse(String.valueOf(comparisonValue))
            )
        }
    }

    enum ComparisonOperator {
        EQ {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isEqualTo(anchorDate)
            }
        },
        NEQ {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isNotEqualTo(anchorDate)
            }
        },
        GT {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isAfter(anchorDate)
            }
        },
        GTE {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isAfterOrEqualTo(anchorDate)
            }
        },
        LT {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isBefore(anchorDate)
            }
        },
        LTE {
            @Override
            void compare(LocalDate actualDate, LocalDate anchorDate) {
                assertThat(actualDate).isBeforeOrEqualTo(anchorDate)
            }
        }

        abstract void compare(LocalDate actualDate, LocalDate anchorDate)
    }

    private static LocalDate parse(String date) {
        LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE)
    }

}
