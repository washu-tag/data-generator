package edu.washu.tag.generator.metadata.pixels

import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class NestedZipCachedPixelSpec extends ZippedCachedPixelSpec {

    private String innerZipName

    NestedZipCachedPixelSpec(String url, String localZipName, String innerZipName, String pathWithinZip, String localPath) {
        super(url, localZipName, pathWithinZip, localPath)
        this.innerZipName = innerZipName
    }

    @Override
    protected String localDownloadName() {
        localZipName
    }

    @Override
    protected void extractFile(File download) {
        final ZipFile outerZip = new ZipFile(download)
        final Path innerZipDir = Files.createTempDirectory('temp_download')
        final Path innerZip = innerZipDir.resolve('temp.zip')
        innerZipDir.toFile().deleteOnExit()

        Files.copy(
            outerZip.getInputStream(
                outerZip.entries().find {
                    it.name == innerZipName
                } as ZipEntry
            ),
            innerZip
        )

        super.extractFile(innerZip.toFile())
    }

}
