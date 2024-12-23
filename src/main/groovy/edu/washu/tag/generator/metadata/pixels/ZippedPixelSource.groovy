package edu.washu.tag.generator.metadata.pixels

import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZippedPixelSource extends PixelSource {

    protected String localZipName
    protected String pathWithinZip

    ZippedPixelSource(String url, String localZipName, String pathWithinZip, String localPath) {
        this.url = url
        this.localZipName = localZipName
        this.pathWithinZip = pathWithinZip
        this.localPath = localPath
    }

    static ZippedPixelSource ofRsnaTestData(String zipPath) {
        new ZippedPixelSource(
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
            fullPath().toPath()
        )
    }

}
