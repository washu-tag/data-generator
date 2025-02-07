package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.seriesTypes.xa.GenericAngiography
import edu.washu.tag.generator.metadata.seriesTypes.xa.OriginalFluoroscopy

class OutsideXRayAngiography extends XRayAngiography {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        (1 .. 15).collect { index ->
            index in [0, 1, 6] ? new OriginalFluoroscopy() : new GenericAngiography()
        }
    }

}
