package edu.washu.tag.generator.metadata.protocols

import edu.washu.tag.generator.metadata.CodedTriplet
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.scanners.OutsideGEInfinia
import edu.washu.tag.generator.metadata.seriesTypes.nm.MyometrixResults
import edu.washu.tag.generator.metadata.seriesTypes.nm.NmGatedTomo
import edu.washu.tag.generator.metadata.seriesTypes.nm.NmTomo
import edu.washu.tag.generator.metadata.seriesTypes.nm.RestingNm

class OutsideMyocardialPerfusion extends MyocardialPerfusion {

    @Override
    List<SeriesType> getAllSeriesTypes() {
        [
                new RestingNm() {
                    @Override
                    List<Class<? extends Equipment>> getCompatibleEquipment() {
                        [OutsideGEInfinia]
                    }
                },
                new NmTomo() {
                    @Override
                    List<Class<? extends Equipment>> getCompatibleEquipment() {
                        [OutsideGEInfinia]
                    }
                },
                new NmGatedTomo() {
                    @Override
                    List<Class<? extends Equipment>> getCompatibleEquipment() {
                        [OutsideGEInfinia]
                    }
                },
                new MyometrixResults() {
                    @Override
                    List<Class<? extends Equipment>> getCompatibleEquipment() {
                        [OutsideGEInfinia]
                    }
                }
        ]
    }

}
