package edu.washu.tag.generator.ai.catalog.attribute

import edu.washu.tag.generator.ai.catalog.CodeCache
import org.apache.commons.lang3.RandomUtils

enum DiagnosisCodeDesignator {

    ICD_9 ('I9', 'ICD-9', 'icd9cm_dx', 'code_dotted'),
    ICD_10 ('I10', 'ICD-10-CM', 'icd10cm', 'code')

    final String hl7Representation
    final String fullIdentifier
    final String ctssIdentifier
    final String searchField
    private static final String VALID_CODE_REGEX = '[a-zA-Z0-9.]{3,8}'

    DiagnosisCodeDesignator(String hl7Representation, String fullIdentifier, String ctssIdentifier, String searchField) {
        this.hl7Representation = hl7Representation
        this.fullIdentifier = fullIdentifier
        this.ctssIdentifier = ctssIdentifier
        this.searchField = searchField
    }

    boolean validateCode(String code) {
        if (!code.matches(VALID_CODE_REGEX)) {
            return false
        }
        CodeCache.validateCode(this, code)
    }

    String getUrlForSearch(String search) {
        "https://clinicaltables.nlm.nih.gov/api/${ctssIdentifier}/v3/search?sf=${searchField}&terms=${search}&maxList=500"
    }

    static DiagnosisCodeDesignator randomize() {
        RandomUtils.insecure().randomDouble(0, 1) < 0.95 ? ICD_10 : ICD_9
    }

}