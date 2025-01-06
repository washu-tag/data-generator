package edu.washu.tag.generator.metadata.pixels

class LocalCache {

    private static final File cacheDir = new File('local_cache')

    static {
        cacheDir.mkdir()
    }

    static File cachedFile(String relativePath) {
        new File(cacheDir, relativePath)
    }

}
