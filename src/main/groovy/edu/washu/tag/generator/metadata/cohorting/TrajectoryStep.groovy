package edu.washu.tag.generator.metadata.cohorting

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * One entry in a cohort's trajectory: either a {@link StudyRequest} (a single study) or a
 * {@link BranchStep} (fans the patients reaching this point out into named continuations).
 * Deduced from shape rather than a discriminator field, so existing flat trajectories (all
 * {@link StudyRequest}) need no YAML changes.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes([
        @JsonSubTypes.Type(StudyRequest),
        @JsonSubTypes.Type(BranchStep)
])
interface TrajectoryStep {

    /**
     * Extends each in-progress branch by this step, returning the resulting branches.
     * A {@link StudyRequest} appends itself to every incoming branch unchanged; a
     * {@link BranchStep} fans each incoming branch out into its own named continuations.
     */
    List<TrajectoryBranch> expand(List<TrajectoryBranch> branches)

}
