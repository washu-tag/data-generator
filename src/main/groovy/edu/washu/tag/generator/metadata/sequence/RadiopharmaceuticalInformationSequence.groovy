package edu.washu.tag.generator.metadata.sequence

import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Sequence
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.DicomEncoder
import edu.washu.tag.generator.metadata.enums.contextGroups.PetRadionuclide
import edu.washu.tag.generator.metadata.enums.contextGroups.PetRadiopharmaceutical
import edu.washu.tag.generator.metadata.enums.contextGroups.RouteOfAdministration

import java.time.LocalDateTime
import java.time.LocalTime

class RadiopharmaceuticalInformationSequence extends ArrayList<Item> implements SequenceElement, DicomEncoder {

    @Override
    void addToAttributes(Attributes attributes) {
        final Sequence sequence = attributes.newSequence(Tag.RadiopharmaceuticalInformationSequence, size())
        this.each { item ->
            sequence << item.toSequenceItem()
        }
    }

    static class Item implements DicomEncoder {
        PetRadionuclide petRadionuclide
        String radiopharmaceuticalRoute
        RouteOfAdministration routeCode
        String radiopharmaceuticalVolume
        LocalTime radiopharmaceuticalStartTime
        LocalDateTime radiopharmaceuticalStartDateTime
        LocalTime radiopharmaceuticalStopTime
        LocalDateTime radiopharmaceuticalStopDateTime
        String radionuclideTotalDose
        String radiopharmaceuticalAdministrationEventUid
        String radionuclideHalfLife
        String radionuclidePositronFraction
        String radiopharmaceuticalSpecificActivity
        String radiopharmaceutical
        PetRadiopharmaceutical radiopharmaceuticalCode

        Attributes toSequenceItem() {
            final Attributes attributes = new Attributes()
            final Sequence radionuclideCodeSequence = attributes.newSequence(Tag.RadionuclideCodeSequence, 0)
            if (petRadionuclide != null) {
                radionuclideCodeSequence << petRadionuclide.codedValue.toSequenceItem()
            }
            setIfNonnull(attributes, Tag.RadiopharmaceuticalRoute, VR.LO, radiopharmaceuticalRoute)
            if (routeCode != null) {
                attributes.newSequence(Tag.AdministrationRouteCodeSequence, 1) << routeCode.codedValue.toSequenceItem()
            }
            setIfNonnull(attributes, Tag.RadiopharmaceuticalVolume, VR.DS, radiopharmaceuticalVolume)
            setTimeIfNonnull(attributes, Tag.RadiopharmaceuticalStartTime, radiopharmaceuticalStartTime)
            setDateTimeIfNonnull(attributes, Tag.RadiopharmaceuticalStartDateTime, radiopharmaceuticalStartDateTime)
            setTimeIfNonnull(attributes, Tag.RadiopharmaceuticalStopTime, radiopharmaceuticalStopTime)
            setDateTimeIfNonnull(attributes, Tag.RadiopharmaceuticalStopDateTime, radiopharmaceuticalStopDateTime)
            setIfNonnull(attributes, Tag.RadionuclideTotalDose, VR.DS, radionuclideTotalDose)
            setIfNonnull(attributes, Tag.RadiopharmaceuticalAdministrationEventUID, VR.UI, radiopharmaceuticalAdministrationEventUid)
            setIfNonnull(attributes, Tag.RadionuclideHalfLife, VR.DS, radionuclideHalfLife)
            setIfNonnull(attributes, Tag.RadionuclidePositronFraction, VR.DS, radionuclidePositronFraction)
            setIfNonnull(attributes, Tag.RadiopharmaceuticalSpecificActivity, VR.DS, radiopharmaceuticalSpecificActivity)
            setIfNonnull(attributes, Tag.Radiopharmaceutical, VR.LO, radiopharmaceutical)
            if (radiopharmaceuticalCode != null) {
                attributes.newSequence(Tag.RadiopharmaceuticalCodeSequence, 1) << radiopharmaceuticalCode.codedValue.toSequenceItem()
            }
            attributes
        }
    }

}
