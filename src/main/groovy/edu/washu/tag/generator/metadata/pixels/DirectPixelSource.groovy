package edu.washu.tag.generator.metadata.pixels

class DirectPixelSource extends PixelSource {

    DirectPixelSource(String url) {
        this.url = url
        localPath = url.split('/')[-1]
    }

    @Override
    protected String localDownloadName() {
        localPath
    }

    @Override
    protected void extractFile(File download) {

    }

}
