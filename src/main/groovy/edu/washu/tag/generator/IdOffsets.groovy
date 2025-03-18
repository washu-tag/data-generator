package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.patient.EpicId
import edu.washu.tag.generator.metadata.patient.MainId
import edu.washu.tag.generator.metadata.patient.PatientIdEncoder
import edu.washu.tag.generator.metadata.patient.SimplePatientIdEncoder
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.concurrent.ThreadLocalRandom

class IdOffsets {

    int mainIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)
    int epicIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)
    int studyIdOffset = 200000000 + ThreadLocalRandom.current().nextInt(700000000)

    List<PatientIdEncoder> getPatientIdEncodersFromOffset(int offset) {
        [
            new SimplePatientIdEncoder(MainId, mainIdOffset + offset),
            new SimplePatientIdEncoder(EpicId, epicIdOffset + offset)
        ]
    }

    SequentialIdGenerator getStudyIdEncoderFromOffset(int offset) {
        new SequentialIdGenerator().currentId(studyIdOffset + offset)
    }

}
