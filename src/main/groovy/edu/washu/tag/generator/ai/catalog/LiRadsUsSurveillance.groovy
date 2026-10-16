package edu.washu.tag.generator.ai.catalog

import com.fasterxml.jackson.annotation.JsonPropertyDescription
import edu.washu.tag.generator.ai.StudyRep
import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.protocols.Ultrasound
import edu.washu.tag.util.FileIOUtils

class LiRadsUsSurveillance extends LiRadsReport {

    private static final String BOILERPLATE = FileIOUtils.readResource('lirads_us_boilerplate.txt')

    @JsonPropertyDescription('LI-RADS ultrasound visualization score (A, B, or C)')
    String visualizationScore

    @Override
    String getUserMessage(Study study, StudyRep studyRep) {
        super.getUserMessage(study, studyRep) + 'This is a LI-RADS US Surveillance v2017 ultrasound report. The technique section should briefly describe grayscale and Doppler ultrasound of the liver. ' +
            'The category must be exactly one of: US-1 (negative: no observation or only definitely benign observations), ' +
            'US-2 (subthreshold: observation smaller than 10 mm that is not definitely benign), or US-3 (positive: observation 10 mm or larger, or new venous thrombus). ' +
            'The visualizationScore must be exactly one of A (no or minimal limitations), B (moderate limitations), or C (severe limitations). ' +
            'The findings can optionally include the visualizationScore, but should also describe lesions with hepatic segment numbers, measurements, and/or defining features. ' +
            'The impression should state the category and visualization score and recommend follow-up: repeat ultrasound in 6 months for US-1, ' +
            'repeat ultrasound in 3 to 6 months for US-2, or multiphase contrast-enhanced CT or MRI for US-3. When stated in the impression section, sometimes format the ' +
            'category with a space after US instead of a dash.'
    }

    @Override
    Boolean checkApplicability(Study study) {
        study.protocol instanceof Ultrasound
    }

    @Override
    protected <S extends ReportTextBuilder<?, S>> S addBoilerplate(S textBuilder) {
        textBuilder.add(BOILERPLATE)
    }

}
