package edu.washu.tag.generator.metadata.cohorting

import com.fasterxml.jackson.annotation.JsonIgnore

class BranchStep implements TrajectoryStep {

    List<BranchOption> branch

    @JsonIgnore
    @Override
    List<TrajectoryBranch> expand(List<TrajectoryBranch> branches) {
        branches.collectMany { incoming ->
            branch.collectMany { option ->
                final List<TrajectoryBranch> seed = [
                    new TrajectoryBranch(
                        name: "${incoming.name}/${option.name}",
                        numPatients: option.numPatients,
                        steps: incoming.steps
                    )
                ]
                option.steps.inject(seed) { branchesSoFar, step -> step.expand(branchesSoFar) }
            }
        }
    }

}
