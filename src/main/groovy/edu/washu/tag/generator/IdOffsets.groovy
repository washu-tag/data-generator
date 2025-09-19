package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.patient.EpicId
import edu.washu.tag.generator.metadata.patient.LegacyId
import edu.washu.tag.generator.metadata.patient.MainId
import edu.washu.tag.generator.metadata.patient.PatientIdEncoder
import edu.washu.tag.generator.metadata.patient.SimplePatientIdEncoder
import edu.washu.tag.generator.util.RandomGenUtils
import edu.washu.tag.generator.util.SequentialIdGenerator

import java.util.concurrent.ThreadLocalRandom

/**
 * Now that I've added IDs that are conditionally applied to reports based on version number, this gets a little
 * odd. I intentionally allocate the ID even when we don't use it to avoid the scenario of IDs looking like
 * 1, 2, 3, 4, ..., 3000, 3001, 5000, 5001, 5002 and instead 1, 2, 4, 5, 6, 7, 9, ...
 */
class IdOffsets {

    int mainIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)
    int epicIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)
    int studyIdOffset = 200000000 + ThreadLocalRandom.current().nextInt(700000000)
    int legacyIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)
    int legacyStandaloneIdOffset = RandomGenUtils.randomId(RandomGenUtils.DEFAULT_NUM_DIGITS)

    List<PatientIdEncoder> getPatientIdEncodersFromOffset(int offset) {
        [
            new SimplePatientIdEncoder(MainId, mainIdOffset + offset),
            new SimplePatientIdEncoder(EpicId, epicIdOffset + offset),
            new SimplePatientIdEncoder(LegacyId, legacyIdOffset + offset)
        ]
    }

    SequentialIdGenerator getStudyIdEncoderFromOffset(int offset) {
        new SequentialIdGenerator().currentId(studyIdOffset + offset)
    }

}
