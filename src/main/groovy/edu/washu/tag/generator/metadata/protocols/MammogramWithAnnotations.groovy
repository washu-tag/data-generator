package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.seriesTypes.pr.GspsMammographyAnnotations

class MammogramWithAnnotations extends MammogramSingleView {

    private static final SeriesType annotations = new GspsMammographyAnnotations()

    @Override
    List<SeriesType> getAllSeriesTypes() {
        super.getAllSeriesTypes() + [annotations] as List<SeriesType>
    }

}
