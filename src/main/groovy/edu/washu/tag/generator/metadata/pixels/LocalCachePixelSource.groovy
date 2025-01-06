package edu.washu.tag.generator.metadata.pixels

import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import org.dcm4che3.data.Attributes
import org.dcm4che3.io.DicomInputStream

class LocalCachePixelSource implements PixelSource {

    private static final Map<String, Attributes> knownImages = [:]
    String pixelKey

    @Override
    Attributes produceImage(Study study, Series series) {
        knownImages.computeIfAbsent(
            pixelKey,
            {
                new DicomInputStream(LocalCache.cachedFile(pixelKey)).withCloseable { dicomStream ->
                    final Attributes dataset = dicomStream.readDataset()
                    dataset.addAll(dicomStream.readFileMetaInformation())
                    dataset
                }
            }
        )
    }

}
