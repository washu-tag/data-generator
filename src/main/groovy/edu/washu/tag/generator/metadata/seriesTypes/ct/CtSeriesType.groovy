package edu.washu.tag.generator.metadata.seriesTypes.ct

import org.dcm4che3.data.UID
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.series.CtImageModule
import edu.washu.tag.generator.metadata.module.series.ImagePlaneModule
import edu.washu.tag.generator.metadata.scanners.CtScanner
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.ImageType
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.scanners.PhilipsGeminiR35
import edu.washu.tag.generator.metadata.scanners.SiemensBiographTruePoint64
import edu.washu.tag.generator.metadata.series.CtSeries
import edu.washu.tag.generator.util.StringReplacements

abstract class CtSeriesType extends SeriesType {

    @Override
    String getModality() {
        'CT'
    }

    @Override
    String getSopClassUid() {
        UID.CTImageStorage
    }

    @Override
    List<Class<? extends Equipment>> getCompatibleEquipment() {
        [SiemensBiographTruePoint64, PhilipsGeminiR35]
    }

    @Override
    ImageType getImageType(Equipment equipment) {
        resolveImageType(equipment as CtScanner)
    }

    @Override
    Class<? extends Series> seriesClass() {
        CtSeries
    }

    // run before GeneralSeriesModule so the series description can name the chosen values
    @Override
    List<SeriesLevelModule> allSeriesModules() {
        [new ImagePlaneModule(), new CtImageModule()] + super.allSeriesModules() as List<SeriesLevelModule>
    }

    @Override
    List<InstanceLevelModule> additionalInstanceModules() {
        [new ImagePixelModule()]
    }

    @Override
    String resolveSeriesDescription(Series series, BodyPart bodyPartExamined) {
        final CtSeries ctSeries = series as CtSeries
        String description = super.resolveSeriesDescription(series, bodyPartExamined)
        description = fillPlaceholder(description, StringReplacements.SLICE_THICKNESS, ctSeries.sliceThickness)
        fillPlaceholder(description, StringReplacements.CONVOLUTION_KERNEL, ctSeries.convolutionKernel)
    }

    String sampleConvolutionKernel(Equipment equipment) {
        null
    }

    abstract ImageType resolveImageType(CtScanner equipment)

    private String fillPlaceholder(String description, String placeholder, Object value) {
        if (!description?.contains(placeholder)) {
            return description
        }
        if (value == null) {
            throw new IllegalStateException("${getClass().simpleName} produced series description '${description}' containing ${placeholder} without a value for it")
        }
        description.replace(placeholder, String.valueOf(value))
    }

}
