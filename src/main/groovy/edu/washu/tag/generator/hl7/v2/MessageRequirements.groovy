package edu.washu.tag.generator.hl7.v2

import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.hl7.v2.model.TransportationMode
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class MessageRequirements {

    int numAttendingDoctors = 1
    boolean includePatientAlias = false
    boolean specifyAddress = true
    boolean extendedPid = true
    ReportStatus orcStatus = null
    TransportationMode transportationMode = null
    String reasonForStudy = 'Chest pain'
    boolean malformObrInterpretersAndTech = true
    int numAsstInterpreters = 1
    boolean includeObx = true
    boolean raceUnavailable = false

}
