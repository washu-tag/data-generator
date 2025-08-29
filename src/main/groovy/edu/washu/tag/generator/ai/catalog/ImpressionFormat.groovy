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
    },
    FINDINGS_AND_IMPRESSION {
        @Override
        String serializeImpression(String impression) {
            "IMPRESSION:\nFINDINGS AND IMPRESSION:\n${impression}"
        }
    },
    FINDINGS_AMP_IMPRESSION {
        @Override
        String serializeImpression(String impression) {
            "IMPRESSION:\nFINDINGS & IMPRESSION:\n${impression}"
        }
    }

    abstract String serializeImpression(String impression)

}