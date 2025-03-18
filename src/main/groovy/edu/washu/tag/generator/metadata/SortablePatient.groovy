package edu.washu.tag.generator.metadata

class SortablePatient {

    String patientInstanceUID
    List<SortableStudy> studies

    SortablePatient(Patient fullRepresentation) {
        patientInstanceUID = fullRepresentation.patientInstanceUid
        studies = fullRepresentation.studies.collect { study ->
            new SortableStudy(
                studyInstanceUID: study.studyInstanceUid,
                studyDateTime: study.studyDateTime()
            )
        }.sort()
    }

}
