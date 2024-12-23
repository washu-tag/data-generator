package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.enums.ConversionType

trait SecondaryCaptureEquipment implements Equipment {

    @JsonIgnore
    abstract String getDeviceId()

    @JsonIgnore
    abstract ConversionType getConversionType()

    @Override
    Institution getInstitution() {
        null
    }

    @Override
    String getStationName() {
        null
    }

    @Override
    String getProtocolName(Study study, Series series) {
        null
    }

    @Override
    String getDeviceSerialNumber() {
        null
    }

    @Override
    void encode(Attributes attributes) {
        encodeSecondaryCaptureEquipment(attributes)
    }

    // TODO: this is split off into a separate method because if I try to call SecondaryCaptureEquipment.super.encode(attributes), it fails (on the command line only??) with "The usage of 'Class.this' and 'Class.super' is only allowed in nested/inner classes." That doesn't make any sense, because it's the standard way to handle trait conflicts. I must be missing/overlooking something.
    void encodeSecondaryCaptureEquipment(Attributes attributes) {
        setIfNonnull(attributes, Tag.ConversionType, VR.CS, conversionType.dicomRepresentation)
        setIfNonnull(attributes, Tag.SecondaryCaptureDeviceID, VR.LO, deviceId)
        setIfNonnull(attributes, Tag.SecondaryCaptureDeviceManufacturer, VR.LO, manufacturer.dicomRepresentation)
        setIfNonnull(attributes, Tag.SecondaryCaptureDeviceManufacturerModelName, VR.LO, modelName)
        setIfNonempty(attributes, Tag.SecondaryCaptureDeviceSoftwareVersions, VR.LO, softwareVersions)
    }

}