package edu.washu.tag.generator.metadata

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.VR
import edu.washu.tag.generator.util.RandomGenUtils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

trait DicomEncoder {

    public static final VR_DA_FORMATTER = DateTimeFormatter.ofPattern('uuuuMMdd')
    public static final VR_TM_FORMATTER = DateTimeFormatter.ofPattern('HHmmss') // not technically true, but good enough for now
    public static final VR_DT_FORMATTER = DateTimeFormatter.ofPattern('uuuuMMddHHmmss') // not technically true, but good enough for now
    public static final EnumeratedDistribution<Closure<String>> dateTimeFormattingRandomizer = RandomGenUtils.setupWeightedLottery([
        ({ dateTimeString -> dateTimeString }) : 80,
        ({ dateTimeString -> dateTimeString + '.0' }) : 10,
        ({ dateTimeString -> dateTimeString + '.000' }) : 10
    ]) as EnumeratedDistribution<Closure>

    void setDate(Attributes attributes, int tag, LocalDate date) {
        attributes.setString(tag, VR.DA, date.format(VR_DA_FORMATTER))
    }

    void setDateIfNonnull(Attributes attributes, int tag, LocalDate date) {
        if (date != null) {
            setDate(attributes, tag, date)
        }
    }

    void setTime(Attributes attributes, int tag, LocalTime time) {
        attributes.setString(tag, VR.TM, time.format(VR_TM_FORMATTER))
    }

    void setTimeIfNonnull(Attributes attributes, int tag, LocalTime time) {
        if (time != null) {
            setTime(attributes, tag, time)
        }
    }

    void setDateTime(Attributes attributes, int tag, LocalDateTime dateTime) {
        attributes.setString(tag, VR.DT, randomizeDateTimeSerialization(dateTime) ?: '')
    }

    void setDateTimeIfNonnull(Attributes attributes, int tag, LocalDateTime dateTime) {
        if (dateTime != null) {
            setDateTime(attributes, tag, dateTime)
        }
    }

    void setIfNonnull(Attributes attributes, int tag, VR vr, String value) {
        if (value != null) {
            attributes.setString(tag, vr, value)
        }
    }

    void setIfNonempty(Attributes attributes, int tag, VR vr, List<String> value) {
        if (value != null && !value.isEmpty()) {
            attributes.setString(tag, vr, value as String[])
        }
    }

    String randomizeDateTimeSerialization(LocalDateTime dateTime) {
        if (dateTime != null) {
            dateTimeFormattingRandomizer.sample()(dateTime.format(VR_DT_FORMATTER))
        } else {
            null
        }
    }

}