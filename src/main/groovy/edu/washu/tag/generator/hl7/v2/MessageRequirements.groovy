package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class MessageRequirements {

    int numPatientIds = 1
    int numAttendingDoctors = 1
    boolean includePatientAlias = false
    boolean specifyAddress = true
    boolean extendedPid = true
    ReportStatus orcStatus = null
    String reasonForStudy = 'Chest pain'
    boolean malformObrInterpretersAndTech = true
    int numAsstInterpreters = 1
    boolean includeObx = true
    boolean raceUnavailable = false

}
