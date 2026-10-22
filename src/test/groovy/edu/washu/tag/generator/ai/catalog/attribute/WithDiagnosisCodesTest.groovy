package edu.washu.tag.generator.ai.catalog.attribute

import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.DiagnosisReport
import edu.washu.tag.generator.metadata.Diagnosis
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

class WithDiagnosisCodesTest {

    @Test
    void testPlainCommaSeparatedCodesValidateAsBefore() {
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'I63.9', codeMeaning: 'Cerebral infarction, unspecified'),
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'Z03.89', codeMeaning: 'Encounter for observation for other suspected diseases and conditions ruled out')
        ])
        final DiagnosisReport report = new DiagnosisReport(designator: DiagnosisCodeDesignator.ICD_10)
        report.setDiagnoses('I63.9,Z03.89')

        assertTrue(report.validateCodes().passed)
        assertEquals(report.parsedCodes, ['I63.9', 'Z03.89'])
    }

    @Test
    void testCodeWithAppendedDescriptionIsTolerated() {
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'I63.9', codeMeaning: 'Cerebral infarction, unspecified')
        ])
        final DiagnosisReport report = new DiagnosisReport(designator: DiagnosisCodeDesignator.ICD_10)
        // GPT's observed real failure mode: code and human-readable meaning joined by an em dash, with the
        // description itself containing a comma - the exact shape that broke the stroke cohort generation.
        report.setDiagnoses('I63.9 — Cerebral infarction, unspecified')

        assertTrue(report.validateCodes().passed, report.validateCodes().failureCause)
        assertEquals(report.parsedCodes, ['I63.9'])
    }

    @Test
    void testCodeWithAppendedDescriptionToleratesHyphenAndEnDashToo() {
        CodeCache.codes.put(DiagnosisCodeDesignator.ICD_10, [
            new Diagnosis(designator: DiagnosisCodeDesignator.ICD_10, code: 'Z03.89', codeMeaning: 'Encounter for observation ruled out')
        ])
        final DiagnosisReport hyphen = new DiagnosisReport(designator: DiagnosisCodeDesignator.ICD_10)
        hyphen.setDiagnoses('Z03.89 - Encounter for observation ruled out')
        assertTrue(hyphen.validateCodes().passed)
        assertEquals(hyphen.parsedCodes, ['Z03.89'])

        final DiagnosisReport enDash = new DiagnosisReport(designator: DiagnosisCodeDesignator.ICD_10)
        enDash.setDiagnoses('Z03.89 – Encounter for observation ruled out')
        assertTrue(enDash.validateCodes().passed)
        assertEquals(enDash.parsedCodes, ['Z03.89'])
    }

    @Test
    void testGarbageWithNoRecoverableCodeStillFails() {
        final DiagnosisReport report = new DiagnosisReport(designator: DiagnosisCodeDesignator.ICD_10)
        report.setDiagnoses('not a real diagnosis at all')

        assertFalse(report.validateCodes().passed)
    }

}
