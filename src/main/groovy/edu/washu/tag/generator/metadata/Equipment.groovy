package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.VR
import edu.washu.tag.generator.metadata.enums.Manufacturer

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
trait Equipment implements DicomEncoder {

    @JsonIgnore
    abstract Institution getInstitution()

    @JsonIgnore
    abstract Manufacturer getManufacturer()

    @JsonIgnore
    abstract String getModelName()

    @JsonIgnore
    abstract String getStationName()

    @JsonIgnore
    abstract List<String> getSoftwareVersions()

    @JsonIgnore
    abstract String getDeviceSerialNumber()

    abstract String getProtocolName(Study study, Series series)

    abstract String getTransferSyntaxUID(String sopClassUID)

    int getSeriesNumber(int seriesIndex, SeriesType seriesType) {
        seriesIndex + 1
    }

    void resample() {}

    int readSeriesNumberFromIndexedList(List<Integer> seriesNumbers, int seriesIndex) {
        if (seriesIndex >= seriesNumbers.size()) {
            throw new UnsupportedOperationException()
        }
        seriesNumbers[seriesIndex]
    }

    boolean supportsNonLatinCharacterSets() {
        false
    }

    String serializeName(Person person) {
        person.serializeToDicom(supportsNonLatinCharacterSets())
    }

    String getTransferSyntaxUID(SeriesType seriesType) {
        getTransferSyntaxUID(seriesType.sopClassUid)
    }

    void reportUnsupportedSopClass(String sopClassUid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("SOP Class ${sopClassUid} not supported by this equipment")
    }

    void encode(Attributes attributes) {
        setIfNonnull(attributes, Tag.Manufacturer, VR.LO, manufacturer.dicomRepresentation)
        setIfNonnull(attributes, Tag.InstitutionName, VR.LO, institution?.institutionName)
        setIfNonnull(attributes, Tag.InstitutionAddress, VR.ST, institution?.institutionAddress)
        setIfNonnull(attributes, Tag.StationName, VR.SH, stationName)
        // TODO: Institutional Department Name
        setIfNonnull(attributes, Tag.ManufacturerModelName, VR.LO, modelName)
        setIfNonnull(attributes, Tag.DeviceSerialNumber, VR.LO, deviceSerialNumber)
        setIfNonempty(attributes, Tag.SoftwareVersions, VR.LO, softwareVersions)
    }

}
