package edu.washu.tag.generator.metadata.pixels

import org.dcm4che3.data.Attributes
import org.dcm4che3.io.DicomInputStream

class PixelCache {

    private static final Map<String, Attributes> knownImages = [:]

    static Attributes lookup(String pixelKey) {
        knownImages.computeIfAbsent(
            pixelKey,
            {
                final DirectPixelSource pixelSource = new DirectPixelSource(pixelKey)
                pixelSource.localPath = pixelKey
                new DicomInputStream(pixelSource.fullPath()).withCloseable { dicomStream ->
                    final Attributes dataset = dicomStream.readDataset()
                    dataset.addAll(dicomStream.readFileMetaInformation())
                    dataset
                }
            }
        )
    }

}
