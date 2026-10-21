package edu.washu.tag.generator.metadata.cohorting

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

class StudyRequestTest {

    @Test
    void testNoOptionsReturnsBaseContextUnchangedAndNoChosenOption() {
        final StudyRequest request = new StudyRequest(additionalGenerationContext: 'Base context.')
        final ResolvedGenerationContext resolved = request.resolveGenerationContext()
        assertEquals(resolved.text, 'Base context.')
        assertNull(resolved.chosenOption)
    }

    @Test
    void testOptionsAreAppendedWeightedAndChosenOptionIsReported() {
        final StudyRequest request = new StudyRequest(
            additionalGenerationContext: 'Base context.',
            additionalGenerationContextOptions: ['common': 90, 'rare': 10]
        )

        final Map<String, Integer> counts = [common: 0, rare: 0]
        1000.times {
            final ResolvedGenerationContext resolved = request.resolveGenerationContext()
            assertTrue(resolved.text in ['Base context. common', 'Base context. rare'], resolved.text)
            assertEquals(resolved.chosenOption, resolved.text.endsWith('common') ? 'common' : 'rare')
            counts[resolved.chosenOption]++
        }

        // Not an exact check on a random process - just confirms the weighting clearly favors
        // the heavier option rather than being effectively uniform.
        assertTrue(counts.common > counts.rare * 3, counts.toString())
    }

}
