package edu.washu.tag.generator.metadata

import groovy.transform.Sortable

import java.time.LocalDateTime

@Sortable(includes = 'studyDateTime')
class SortableStudy {

    String patientInstanceUID
    String studyInstanceUID
    LocalDateTime studyDateTime

}
