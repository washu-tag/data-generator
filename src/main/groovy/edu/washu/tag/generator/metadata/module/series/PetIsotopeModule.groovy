package edu.washu.tag.generator.metadata.module.series

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.contextGroups.PetRadionuclide
import edu.washu.tag.generator.metadata.enums.contextGroups.PetRadiopharmaceutical
import edu.washu.tag.generator.metadata.enums.contextGroups.RouteOfAdministration
import edu.washu.tag.generator.metadata.module.SeriesLevelModule
import edu.washu.tag.generator.metadata.protocols.PetStudy
import edu.washu.tag.generator.metadata.scanners.PetScanner
import edu.washu.tag.generator.metadata.sequence.RadiopharmaceuticalInformationSequence
import edu.washu.tag.generator.metadata.series.PtSeries

import java.time.LocalTime

class PetIsotopeModule implements SeriesLevelModule<PtSeries> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, PtSeries series) {
        final PetStudy protocol = study.protocol as PetStudy
        final PetScanner scanner = series.scanner as PetScanner
        final PetRadiopharmaceutical petRadiopharmaceutical = protocol.currentPharmaceutical
        final PetRadionuclide petRadionuclide = petRadiopharmaceutical.petRadionuclide
        final LocalTime injectionTime = series.seriesTime.minusSeconds(protocol.currentInjectionOffset)
        final RadiopharmaceuticalInformationSequence radiopharmaceuticalInformationSequence = new RadiopharmaceuticalInformationSequence()
        final RadiopharmaceuticalInformationSequence.Item sequenceItem = new RadiopharmaceuticalInformationSequence.Item()
        radiopharmaceuticalInformationSequence << sequenceItem
        series.setRadiopharmaceuticalInformationSequence(radiopharmaceuticalInformationSequence)
        sequenceItem.setPetRadionuclide(petRadionuclide)
        if (scanner.encodeRoute()) {
            sequenceItem.setRadiopharmaceuticalRoute(RouteOfAdministration.INTRAVENOUS_ROUTE.codedValue.codeMeaning)
            sequenceItem.setRouteCode(RouteOfAdministration.INTRAVENOUS_ROUTE)
        }
        sequenceItem.setRadiopharmaceuticalStartTime(injectionTime)
        if (scanner.encodeRadiopharmaceuticalStartDateTime()) {
            sequenceItem.setRadiopharmaceuticalStartDateTime(series.seriesDate.atTime(injectionTime))
        }
        sequenceItem.setRadionuclideTotalDose(protocol.currentDosage.toString())
        sequenceItem.setRadionuclideHalfLife(petRadionuclide.halfLife)
        sequenceItem.setRadionuclidePositronFraction(petRadionuclide.positronFraction)
        if (scanner.encodeRadiopharmaceutical()) {
            sequenceItem.setRadiopharmaceutical(petRadiopharmaceutical.codedValue.codeMeaning)
            sequenceItem.setRadiopharmaceuticalCode(petRadiopharmaceutical)
        }
    }

}
