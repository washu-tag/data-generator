package edu.washu.tag.generator.ai.catalog

enum FindingsFormat {

    POST_PARENS {
        @Override
        List<String> serializeFindings(Map<String, String> findingsMap) {
            findingsMap.collect { bodyPart, findings ->
                "FINDINGS (${bodyPart}): ${findings}".toString()
            }
        }
    },
    STANDALONE {
        @Override
        List<String> serializeFindings(Map<String, String> findingsMap) {
            ['FINDINGS:\n'] + findingsMap.collect { bodyPart, findings ->
                "${bodyPart}: ${findings}".toString()
            }
        }
    },
    POST_PLAIN {
        @Override
        List<String> serializeFindings(Map<String, String> findingsMap) {
            findingsMap.collect { bodyPart, findings ->
                "FINDINGS ${bodyPart}: ${findings}".toString()
            }
        }
    }

    abstract List<String> serializeFindings(Map<String, String> findingsMap)

    List<String> parseAndSerialize(List<String> findings) {
        serializeFindings(
            findings.collectEntries { finding ->
                final int colonIndex = finding.indexOf(':')
                [(finding.substring(0, colonIndex)): (finding.substring(colonIndex + 1).trim())]
            }
        )
    }

}