package edu.washu.tag.generator.metadata.cohorting

import edu.washu.tag.generator.ai.catalog.CodeCache
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCodeDesignator
import edu.washu.tag.generator.metadata.Diagnosis
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.util.RandomGenUtils
import org.apache.commons.lang3.RandomUtils

import java.util.function.Function

enum CodeStrategy {

    ONLY_ONE ({ Map<String, Integer> codes ->
        [RandomGenUtils.setupWeightedLottery(codes).sample()]
    }),
    ONLY_ONE_FIXED_FOR_PATIENT (ONLY_ONE.codeRandomizer) {
        @Override
        List<Diagnosis> randomizeAndActualizeCodes(Study study, Map<String, Integer> weightedCodes) {
            final String patientUid = study.patient.patientInstanceUid
            if (!(fixedForPatientUidCodeLookup.containsKey(patientUid))) {
                fixedForPatientUidCodeLookup.put(
                    patientUid,
                    actualizeCode(ONLY_ONE.codeRandomizer.apply(weightedCodes)[0])
                )
            }
            [fixedForPatientUidCodeLookup[patientUid]]
        }
    },
    STRICT_SUBSET ({ Map<String, Integer> codes ->
        randomizeSubsetOfSize(
            RandomUtils.insecure().randomInt(1, codes.size()),

        )
    }),
    SUBSET ({ Map<String, Integer> codes ->
        randomizeSubsetOfSize(
            RandomUtils.insecure().randomInt(1, codes.size() + 1)
        )
    })

    private final Function<Map<String, Integer>, List<String>> codeRandomizer
    private static final Map<String, Diagnosis> fixedForPatientUidCodeLookup = [:]

    CodeStrategy(Function<Map<String, Integer>, List<String>> randomizer) {
        codeRandomizer = randomizer
    }

    List<Diagnosis> randomizeAndActualizeCodes(Study study, Map<String, Integer> weightedCodes) {
        final List<String> randomizedCodes = codeRandomizer.apply(weightedCodes)
        randomizedCodes.collect { code ->
            actualizeCode(code)
        }
    }

    private List<String> randomizeSubsetOfSize(int subsetSize, Map<String, Integer> weightedCodes) {
        RandomGenUtils.sampleWithoutReplacement(subsetSize, weightedCodes)
    }

    private Diagnosis actualizeCode(String code) {
        RandomGenUtils.randomListEntry(
            CodeCache.resolvePartialCode(DiagnosisCodeDesignator.ICD_10, code)
        )
    }

}