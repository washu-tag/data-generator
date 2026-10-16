package edu.washu.tag.generator.ai.catalog

import edu.washu.tag.generator.ai.catalog.builder.ReportTextBuilder
import edu.washu.tag.util.FileIOUtils

class LiRadsUs2024Surveillance extends LiRadsUsSurveillance {

    private static final String BOILERPLATE_2024 = FileIOUtils.readResource('lirads_2024_us_boilerplate.txt')

    @Override
    protected <S extends ReportTextBuilder<?, S>> S addBoilerplate(S textBuilder) {
        textBuilder.add(BOILERPLATE_2024)
    }

}
