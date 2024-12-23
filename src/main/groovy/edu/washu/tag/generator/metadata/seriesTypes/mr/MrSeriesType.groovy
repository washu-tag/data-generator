package edu.washu.tag.generator.metadata.seriesTypes.mr

import edu.washu.tag.generator.metadata.Instance
import edu.washu.tag.generator.metadata.pixels.PixelSource
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.pixels.ZippedPixelSource
import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.enums.AnatomicalPlane
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.scanners.GEOptimaMR450w
import edu.washu.tag.generator.metadata.scanners.PhilipsAchieva
import edu.washu.tag.generator.metadata.scanners.SiemensAvanto
import edu.washu.tag.generator.util.StringReplacements

abstract class MrSeriesType extends SeriesType {

    AnatomicalPlane anatomicalPlane

    @Override
    String getModality() {
        'MR'
    }

    @Override
    String getSopClassUid() {
        UID.MRImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [
                PhilipsAchieva,
                SiemensAvanto,
                GEOptimaMR450w
        ]
    }

    @Override
    String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined) {
        getSeriesDescriptionRandomizer(scanner).
                sample().
                replace('%PLANE%', anatomicalPlane ? anatomicalPlane.sampleEncodedValue() : '').
                replace(StringReplacements.BODYPART, bodyPartExamined.dicomRepresentation)
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        new ImageType() // TODO
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    PixelSource pixelSourceFor(Series series, Instance instance) {
        ZippedPixelSource.ofRsnaTestData(
            switch (series.bodyPartExamined) {
                case BodyPart.ABDOMEN -> 'PICKER/MRIM50'
                case BodyPart.BRAIN -> 'GEMS/MR/IM229'
                case BodyPart.CHEST -> 'TOSHIBA/PAT00054/STD00055/SFS00056/OBJ00057'
                case BodyPart.HEAD -> 'GEMS/MR/IM223'
                case BodyPart.HEART -> 'PHILIPS/MR4_5/MRHEARTR'
                case BodyPart.PELVIS -> 'TOSHIBA/PAT00042/STD00043/SFS00044/OBJ00045' // TODO: is this even correct? Do I have a better sample?
                default -> throw new RuntimeException("Unknown body part: ${series.bodyPartExamined}")
            }
        )
    }

    abstract EnumeratedDistribution<String> getSeriesDescriptionRandomizer(Equipment scanner)

}
