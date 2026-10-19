package edu.washu.tag.generator.metadata.cohorting

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertThrows
import static org.testng.Assert.expectThrows
import static org.testng.Assert.assertTrue

class CodeStrategyTest {

    private static final Map<String, Integer> CODES = ['C22.0' : 5, 'K74.60' : 5, 'B18.2' : 5]

    @Test
    void testStrictSubset() {
        100.times {
            final List<String> sampled = randomize(CodeStrategy.STRICT_SUBSET, CODES)
            assertTrue(sampled.size() in 1 ..< CODES.size(), sampled.toString())
            assertSubsetWithoutDuplicates(sampled)
        }
    }

    @Test
    void testSubset() {
        final Set<Integer> observedSizes = []
        100.times {
            final List<String> sampled = randomize(CodeStrategy.SUBSET, CODES)
            assertTrue(sampled.size() in 1 .. CODES.size(), sampled.toString())
            assertSubsetWithoutDuplicates(sampled)
            observedSizes << sampled.size()
        }
        assertTrue(CODES.size() in observedSizes)
    }

    @Test
    void testStrictSubsetOfSingleCode() {
        assertEquals(randomize(CodeStrategy.STRICT_SUBSET, ['C22.0' : 1]), ['C22.0'])
    }

    @Test
    void testValidTrajectories() {
        cohort(step(['C22.0']), step([]), step([])).validateDiagnosisCodes()
        cohort(step(['C22.0'], CodeStrategy.SUBSET), step(['K74.60'], CodeStrategy.ONLY_ONE)).validateDiagnosisCodes()
        cohort(new StudyRequest(name: 'weighted', diagnosisCodesWeighted: ['C22.0' : 3])).validateDiagnosisCodes()
    }

    @Test
    void testFixedForPatientWithoutInitialCodes() {
        final IllegalArgumentException exception = expectThrows(IllegalArgumentException) {
            cohort(step([]), step(['C22.0'])).validateDiagnosisCodes()
        }
        assertTrue(exception.message.contains('no earlier step fixes a code'), exception.message)
    }

    @Test
    void testFixedCodeNotCarriedIntoOtherStrategies() {
        assertThrows(IllegalArgumentException) {
            cohort(step(['C22.0']), step([], CodeStrategy.SUBSET)).validateDiagnosisCodes()
        }
    }

    @Test
    void testEarlierNonFixedStepDoesNotFixCode() {
        assertThrows(IllegalArgumentException) {
            cohort(step(['C22.0'], CodeStrategy.ONLY_ONE), step([])).validateDiagnosisCodes()
        }
    }

    private static List<String> randomize(CodeStrategy strategy, Map<String, Integer> codes) {
        strategy.@codeRandomizer.apply(codes)
    }

    private static void assertSubsetWithoutDuplicates(List<String> sampled) {
        assertTrue(CODES.keySet().containsAll(sampled), sampled.toString())
        assertEquals(sampled.unique(false).size(), sampled.size())
    }

    private static StudyRequest step(List<String> codes, CodeStrategy strategy = CodeStrategy.ONLY_ONE_FIXED_FOR_PATIENT) {
        new StudyRequest(name: "step${codes}", diagnosisCodes: codes, codeStrategy: strategy)
    }

    private static ResolvedCohort cohort(StudyRequest... steps) {
        new SpecializedCohort(name: 'test', trajectory: steps as List<TrajectoryStep>).resolve()[0]
    }

}
