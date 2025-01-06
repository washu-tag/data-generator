package edu.washu.tag.generator.metadata.pixels

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

abstract class CachedPixelSpec implements PixelSpecification {

    protected String url
    protected String localPath

    protected abstract String localDownloadName()

    protected abstract void extractFile(File download)

    String getLocalPath() {
        localPath
    }

    File cache() {
        final File fullPath = LocalCache.cachedFile(localPath)
        if (fullPath.exists()) {
            return fullPath
        }
        final File downloadedObject = LocalCache.cachedFile(localDownloadName())
        if (!downloadedObject.exists()) {
            HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build(),
                HttpResponse.BodyHandlers.ofFile(downloadedObject.toPath())
            )
        }
        extractFile(downloadedObject)
        fullPath
    }

    @Override
    PixelSource generateSource() {
        cache()
        new LocalCachePixelSource(pixelKey: localPath)
    }

}
