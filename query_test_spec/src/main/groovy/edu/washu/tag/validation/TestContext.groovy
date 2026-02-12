package edu.washu.tag.validation

class TestContext implements Serializable {

    public static final String LAKE_BUCKET_KEY = '%LAKE_BUCKET%'

    String lakeBucketName = 'lake'

    Map<String, String> buildStringReplacements() {
        [(LAKE_BUCKET_KEY): lakeBucketName]
    }

    String replaceStrings(String input) {
        if (input == null) {
            return null
        }
        String modified = input
        buildStringReplacements().each { entry ->
            modified = modified.replace(entry.key, entry.value)
        }
        modified
    }

}
