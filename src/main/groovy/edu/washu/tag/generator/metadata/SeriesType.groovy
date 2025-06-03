package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.Laterality
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.module.InstanceLevelModule
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.module.instance.ImagePixelModule
import edu.washu.tag.generator.metadata.module.instance.SopCommonModule
import edu.washu.tag.generator.metadata.module.series.GeneralSeriesModule
import edu.washu.tag.generator.metadata.pixels.PixelSpecification
import edu.washu.tag.generator.util.StringReplacements

abstract class SeriesType {

    SpecificationParameters specificationParameters

    abstract String getModality()

    abstract String getSopClassUid() // We're not going to consider weird data that's technically compliant where there are multiple SOP classes within a series

    abstract String getSeriesDescription(Equipment scanner, BodyPart bodyPartExamined)

    abstract List<Class<? extends Equipment>> getCompatibleEquipment()

    abstract ImageType getImageType(Equipment equipment)

    final List<Instance> produceInstances(Patient patient, Study study, Equipment equipment, Series series) {
        specificationParameters.createFullSopInstances ? createCustomizedInstances(patient, study, equipment, series) : createGenericInstances(patient, study, equipment, series)
    }

    final List<Instance> createGenericInstances(Patient patient, Study study, Equipment equipment, Series series, int numInstances = 1) {
        (0 ..< numInstances).collect {
            final Instance instance = new Instance()
            collectModules().each { module ->
                module.apply(specificationParameters, patient, study, equipment, series, instance, it)
            }
            if (specificationParameters.includePixelData) {
                final PixelSpecification pixelSpec = pixelSpecFor(series, instance)
                if (pixelSpec != null) {
                    instance.setPixelSource(pixelSpec.generateSource())
                }
            }
            instance
        }
    }

    List<Instance> createCustomizedInstances(Patient patient, Study study, Equipment equipment, Series series) {
        createGenericInstances(patient, study, equipment, series, producedInstanceCount())
    }

    int producedInstanceCount() {
        1
    }

    List<SeriesLevelModule> allSeriesModules() {
        [new GeneralSeriesModule()] + additionalSeriesModules() as List<SeriesLevelModule>
    }

    List<SeriesLevelModule> additionalSeriesModules() {
        []
    }

    List<InstanceLevelModule> additionalInstanceModules() {
        []
    }

    Class<? extends Series> seriesClass() {
        Series
    }

    SeriesBundling seriesBundling() {
        SeriesBundling.PRIMARY
    }

    Laterality laterality() {
        null
    }

    PixelSpecification pixelSpecFor(Series series, Instance instance) {
        null
    }

    protected String replaceBodyPart(String baseString, BodyPart bodyPartExamined) {
        baseString.replace(StringReplacements.BODYPART, bodyPartExamined.dicomRepresentation).replace('WHOLEBODY', 'WB')
    }

    protected List<InstanceLevelModule> collectModules() {
        final List<InstanceLevelModule> additional = additionalInstanceModules()
        if (!specificationParameters.includePixelData) {
            additional.removeIf { module ->
                module instanceof ImagePixelModule
            }
        }
        [new SopCommonModule()] + additional as List<InstanceLevelModule>
    }

}
