package edu.washu.tag.generator.ai.catalog

enum ImpressionFormat {

    STANDALONE {
        @Override
        String serializeImpression(String impression) {
            "IMPRESSION: \n${impression}"
        }
    },
    FINDINGS_IMPRESSION {
        @Override
        String serializeImpression(String impression) {
            "IMPRESSION:\nFINDINGS/IMPRESSION:\n${impression}"
        }
    }

    abstract String serializeImpression(String impression)

}