package edu.washu.tag.generator.metadata.cohorting

/**
 * One fully- or partially-resolved path through a cohort's trajectory: a patient count and the
 * (possibly still-growing) ordered list of studies those patients will get.
 */
class TrajectoryBranch {

    String name
    int numPatients
    List<StudyRequest> steps

}
