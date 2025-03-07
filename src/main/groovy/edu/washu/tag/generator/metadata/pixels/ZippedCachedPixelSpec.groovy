package edu.washu.tag.generator.metadata.pixels

import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZippedCachedPixelSpec extends CachedPixelSpec {

    protected String localZipName
    protected String pathWithinZip

    ZippedCachedPixelSpec(String url, String localZipName, String pathWithinZip, String localPath) {
        this.url = url
        this.localZipName = localZipName
        this.pathWithinZip = pathWithinZip
        this.localPath = localPath
    }

    static ZippedCachedPixelSpec ofRsnaTestData(String zipPath) {
        new ZippedCachedPixelSpec(
            'https://download.nrg.wustl.edu/pub/data/multivendor_dcm.zip',
            'multivendor_dcm.zip',
            'RSNA2000/' + zipPath,
            zipPath.split('/')[-1]
        )
    }

    @Override
    protected String localDownloadName() {
        localZipName
    }

    @Override
    protected void extractFile(File download) {
        final ZipFile zipFile = new ZipFile(download)

        Files.copy(
            zipFile.getInputStream(
                zipFile.entries().find {
                    it.name == pathWithinZip
                } as ZipEntry
            ),
            LocalCache.cachedFile(localPath).toPath()
        )
    }

}
