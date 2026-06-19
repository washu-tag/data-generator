package edu.washu.tag.generator.util

import edu.washu.tag.generator.hl7.v2.model.HierarchicDesignator

class GenerationConstants {

    public static final String MAIN_HOSPITAL = 'ABC'
    public static final HierarchicDesignator defaultParentAssigningAuth = HierarchicDesignator.simple(MAIN_HOSPITAL)

}
