package edu.washu.tag.generator.metadata

import edu.washu.tag.generator.IdOffsets
import edu.washu.tag.generator.SpecificationParameters

class PopulationContext {

    final SpecificationParameters specificationParameters
    final IdOffsets idOffsets
    long currentAverageStudiesPerPatient
    long generatedSeries
    long generatedStudies

    PopulationContext(SpecificationParameters specificationParameters, IdOffsets idOffsets) {
        this.specificationParameters = specificationParameters
        this.idOffsets = idOffsets
    }

    PopulationContext setCounts(long currentAverageStudiesPerPatient, long generatedSeries, long generatedStudies) {
        this.currentAverageStudiesPerPatient = currentAverageStudiesPerPatient
        this.generatedSeries = generatedSeries
        this.generatedStudies = generatedStudies
        this
    }

}
