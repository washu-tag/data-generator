package edu.washu.tag.generator.metadata.pixels

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class PixelSource {

    protected String url
    protected String localPath
    protected static final File cacheDir = new File('local_cache')

    static {
        cacheDir.mkdir()
    }

    protected abstract String localDownloadName()

    protected abstract void extractFile(File download)

    String getLocalPath() {
        localPath
    }

    void cache() {
        final File fullPath = fullPath()
        if (fullPath.exists()) {
            return
        }
        final File downloadedObject = new File(cacheDir, localDownloadName())
        if (!downloadedObject.exists()) {
            HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build(),
                HttpResponse.BodyHandlers.ofFile(downloadedObject.toPath())
            )
        }
        extractFile(downloadedObject)
    }

    File fullPath() {
        new File(cacheDir, localPath)
    }

}
