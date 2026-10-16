package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.ContrastAgent
import edu.washu.tag.generator.metadata.enums.ContrastPhase
import edu.washu.tag.generator.metadata.seriesTypes.ContrastPhased
import edu.washu.tag.generator.metadata.seriesTypes.ct.Topogram
import edu.washu.tag.generator.metadata.seriesTypes.mr.InOpposedPhaseT1
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertThrows
import static org.testng.Assert.assertTrue

class LiverProtocolsTest {

    @Test
    void testLiverMriExtracellularPhases() {
        final List<SeriesType> seriesTypes = new LiverMri().allSeriesTypes
        assertTrue(seriesTypes.any { it instanceof InOpposedPhaseT1 })
        assertEquals(phasesOf(seriesTypes), [ContrastPhase.PRECONTRAST, ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS, ContrastPhase.DELAYED])
    }

    @Test
    void testLiverMriGadobenatePhases() {
        assertEquals(
            phasesOf(new LiverMri(contrastAgent: ContrastAgent.GADOBENATE).allSeriesTypes),
            [ContrastPhase.PRECONTRAST, ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS, ContrastPhase.DELAYED]
        )
    }

    @Test
    void testLiverMriGadoxetatePhases() {
        assertEquals(
            phasesOf(new LiverMri(contrastAgent: ContrastAgent.GADOXETATE).allSeriesTypes),
            [ContrastPhase.PRECONTRAST, ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS, ContrastPhase.TRANSITIONAL, ContrastPhase.HEPATOBILIARY]
        )
    }

    @Test
    void testLiverCtPrecontrastOptional() {
        final List<ContrastPhase> required = [ContrastPhase.LATE_ARTERIAL, ContrastPhase.PORTAL_VENOUS, ContrastPhase.DELAYED]
        assertEquals(phasesOf(new LiverCt(precontrastFraction: 0).allSeriesTypes), required)
        final List<SeriesType> withPrecontrast = new LiverCt(precontrastFraction: 1).allSeriesTypes
        assertTrue(withPrecontrast[0] instanceof Topogram)
        assertEquals(phasesOf(withPrecontrast), [ContrastPhase.PRECONTRAST] + required)
    }

    @Test
    void testMrOnlyPhasesHaveNoCtLabels() {
        assertThrows(UnsupportedOperationException, { ContrastPhase.TRANSITIONAL.sampleCtLabel() })
        assertThrows(UnsupportedOperationException, { ContrastPhase.HEPATOBILIARY.sampleCtLabel() })
        ContrastPhase.values().each { it.sampleMrLabel() }
    }

    private static List<ContrastPhase> phasesOf(List<SeriesType> seriesTypes) {
        seriesTypes.findAll { it instanceof ContrastPhased }*.phase
    }

}
