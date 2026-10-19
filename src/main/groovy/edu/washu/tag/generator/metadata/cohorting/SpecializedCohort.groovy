package edu.washu.tag.generator.metadata.cohorting

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * A cohort as authored in YAML: a trajectory that may contain {@link BranchStep}s. Never consumed
 * directly by validation, batching, or patient generation - call {@link #resolve()} first.
 */
class SpecializedCohort {

    String name
    int numPatients
    List<TrajectoryStep> trajectory

    /**
     * Expands any {@link BranchStep}s in this cohort's trajectory into separate
     * {@link ResolvedCohort}s, one per branch path, each with its own patient count. A no-branch
     * trajectory resolves to a single {@link ResolvedCohort} with the same name and patient count.
     */
    @JsonIgnore
    List<ResolvedCohort> resolve() {
        final List<TrajectoryBranch> seed = [new TrajectoryBranch(name: name, numPatients: numPatients, steps: [])]
        final List<TrajectoryBranch> resolved = trajectory.inject(seed) { branches, step ->
            step.expand(branches)
        }
        resolved.collect { b ->
            new ResolvedCohort(name: b.name, numPatients: b.numPatients, trajectory: b.steps)
        }
    }

}
