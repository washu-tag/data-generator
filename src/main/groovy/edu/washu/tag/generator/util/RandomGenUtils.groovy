package edu.washu.tag.generator.util

import org.apache.commons.lang3.RandomUtils
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.apache.commons.math3.util.Pair

import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ThreadLocalRandom

class RandomGenUtils {

    public static final int DEFAULT_NUM_DIGITS = 7

    static int randomId(int maxDigits = DEFAULT_NUM_DIGITS) {
        RandomUtils.secure().randomInt(1, Math.pow(10, maxDigits).intValue())
    }

    static String randomIdStr(int maxDigits = DEFAULT_NUM_DIGITS) {
        String.valueOf(randomId(maxDigits))
    }

    static LocalDate randomDate(LocalDate earliestPossible, LocalDate latestPossible) {
        earliestPossible.plusDays(ThreadLocalRandom.current().nextLong(ChronoUnit.DAYS.between(earliestPossible, latestPossible) + 1))
    }

    static LocalTime randomTime() {
        LocalTime.of(
               ThreadLocalRandom.current().nextInt(24),
               ThreadLocalRandom.current().nextInt(60),
               ThreadLocalRandom.current().nextInt(60),
        )
    }

    static LocalTime randomTimeInHourRange(int minimumHour, int maximumHourExclusive) {
        LocalTime.of(
                minimumHour + ThreadLocalRandom.current().nextInt(maximumHourExclusive - minimumHour),
                ThreadLocalRandom.current().nextInt(60),
                ThreadLocalRandom.current().nextInt(60),
        )
    }

    static LocalTime randomStudyTime() {
        randomTimeInHourRange(6, 21) // this way we don't have to worry about SeriesTime values crossing midnight. Also, it seems pretty realistic (MRIs at 2AM seem really weird)
    }

    static <X> X randomListEntry(List<X> list) {
        list.get(ThreadLocalRandom.current().nextInt(0, list.size()))
    }

    static <X> List<X> randomSubset(List<X> list, int subsetSize) {
        final List<X> copy = new ArrayList<>(list)
        (0 ..< subsetSize).collect {
            final int index = randomListEntry(0 ..< copy.size())
            final X element = copy.get(index)
            copy.removeAt(index)
            element
        }
    }

    static <X extends Enum> X randomEnumValue(Class<X> enumClass) {
        randomListEntry(enumClass.getEnumConstants() as List<X>)
    }

    static boolean weightedCoinFlip(int percentTrue) {
        ThreadLocalRandom.current().nextInt(100) < percentTrue
    }

    static <X> EnumeratedDistribution<X> setupWeightedLottery(Map<X, Integer> weights) {
        new EnumeratedDistribution<>(
                weights.collect { item, weight ->
                    new Pair(item, weight.doubleValue())
                }
        )
    }

}
