package edu.washu.tag.generator.metadata.cohorting

/**
 * One named continuation of a {@link BranchStep}: its own patient count and its own trajectory,
 * which may itself contain further branches.
 */
class BranchOption {

    String name
    int numPatients
    List<TrajectoryStep> steps = []

}
