package edu.washu.tag.generator.metadata.cohorting

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

class StudyRequestTest {

    @Test
    void testNoOptionsReturnsBaseContextUnchanged() {
        final StudyRequest request = new StudyRequest(additionalGenerationContext: 'Base context.')
        assertEquals(request.resolveAdditionalGenerationContext(), 'Base context.')
    }

    @Test
    void testOptionsAreAppendedAndWeighted() {
        final StudyRequest request = new StudyRequest(
            additionalGenerationContext: 'Base context.',
            additionalGenerationContextOptions: ['common': 90, 'rare': 10]
        )

        final Map<String, Integer> counts = [common: 0, rare: 0]
        1000.times {
            final String resolved = request.resolveAdditionalGenerationContext()
            assertTrue(resolved in ['Base context. common', 'Base context. rare'], resolved)
            counts[resolved.endsWith('common') ? 'common' : 'rare']++
        }

        // Not an exact check on a random process - just confirms the weighting clearly favors
        // the heavier option rather than being effectively uniform.
        assertTrue(counts.common > counts.rare * 3, counts.toString())
    }

}
