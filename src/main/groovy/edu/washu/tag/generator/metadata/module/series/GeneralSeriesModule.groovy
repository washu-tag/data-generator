package edu.washu.tag.generator.metadata.module.series

import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.SeriesType
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.module.SeriesLevelModule

class GeneralSeriesModule implements SeriesLevelModule<Series> {

    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study, Equipment equipment, Series series) {
        final SeriesType seriesType = series.seriesType
        setModality(series)
        setSeriesInstanceUid(series)
        setSeriesNumber(series)
        series.setLaterality(seriesType.laterality())
        setSeriesDate(study, series)
        setSeriesTime(study, series)
        if (study.performingPhysiciansName != null && seriesType.seriesBundling() == SeriesBundling.PRIMARY) {
            series.setPerformingPhysiciansName(study.performingPhysiciansName)
        }
        setSeriesDescription(study, series)
        setProtocolName(study, series)
        final List<String> operators = study.operatorMap?.get(equipment)
        if (operators != null) {
            series.setOperatorsName(operators)
        }
        series.setBodyPartExamined(study.bodyPartExamined) // TODO: series should be able to overwrite body part
        // TODO: Patient Position
        // TODO: Smallest/Largest Pixel Value
    }

    protected static void setModality(Series series) {
        series.setModality(series.seriesType.modality)
    }

    protected static void setSeriesInstanceUid(Series series) {
        series.setSeriesInstanceUid(UIDUtils.createUID())
    }

    protected static void setSeriesNumber(Series series) {
        final int seriesNumber = series.scanner.getSeriesNumber(series.seriesIndex, series.seriesType)
        if (seriesNumber >= 0) {
            series.setSeriesNumber(seriesNumber.toString())
        }
    }

    protected static void setSeriesDate(Study study, Series series) {
        series.setSeriesDate(study.studyDate.plusDays(study.dateOffsetMap.get(series.seriesType.seriesBundling())))
    }

    protected static void setSeriesTime(Study study, Series series) {
        series.setSeriesTime(
                (study.seriesTimeMap.get(series.seriesType.seriesBundling()) ?: study.studyTime).plusMinutes(5 * series.seriesIndex)
        ) // TODO: each series should not be hardcoded as 5 minutes
    }

    protected static void setSeriesDescription(Study study, Series series) {
        series.setSeriesDescription(series.seriesType.getSeriesDescription(series.scanner, study.bodyPartExamined)) // TODO: series should be able to overwrite body part
    }

    protected static void setProtocolName(Study study, Series series) {
        series.setProtocolName(series.scanner.getProtocolName(study, series))
    }

}
