package edu.washu.tag.generator.metadata.cohorting

import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.YamlObjectMapper
import edu.washu.tag.util.FileIOUtils
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class TrajectoryBranchingTest {

    @Test
    void testHistoricalFlatCohortSpecStillParses() {
        final SpecificationParameters spec = new YamlObjectMapper().readValue(
            FileIOUtils.readResource('historical_cohort_spec.yaml'), SpecificationParameters
        )

        assertEquals(spec.cohorts.size(), 1)
        final ResolvedCohort cohort = spec.cohorts[0]
        assertEquals(cohort.name, 'hcc_surveillance_quick_treatment')
        assertEquals(cohort.numPatients, 30)
        assertEquals(cohort.trajectory*.name, ['surveillance_us_1', 'diagnostic_mri', 'post_tace_ct'])
        assertEquals(cohort.trajectory*.diagnosisCodes, [['K74.60'], ['C22.0'], ['C22.0']])
    }

    @Test
    void testFlatTrajectoryResolvesToItself() {
        final List<StudyRequest> steps = [
            new StudyRequest(name: 'a', diagnosisCodes: ['K74.60']),
            new StudyRequest(name: 'b', diagnosisCodes: ['C22.0'])
        ]
        final SpecializedCohort cohort = new SpecializedCohort(name: 'flat', numPatients: 10, trajectory: steps as List<TrajectoryStep>)

        final List<ResolvedCohort> resolved = cohort.resolve()

        assertEquals(resolved.size(), 1)
        assertEquals(resolved[0].name, 'flat')
        assertEquals(resolved[0].numPatients, 10)
        assertEquals(resolved[0].trajectory, steps)
    }

    @Test
    void testBranchingCohortSpecParsesAndResolves() {
        final SpecificationParameters spec = new YamlObjectMapper().readValue(
            FileIOUtils.readResource('branching_cohort_spec.yaml'), SpecificationParameters
        )

        // SpecificationParameters.setCohorts() resolves branches eagerly, so this is already expanded.
        assertEquals(spec.cohorts*.name.sort(), [
            'hcc_quick_treatment_family/adequate_response',
            'hcc_quick_treatment_family/equivocal_response'
        ])
        spec.cohorts.each { cohort ->
            assertEquals(cohort.numPatients, 15)
            assertEquals(cohort.trajectory*.name, ['surveillance_us', 'post_tace_ct'])
        }
    }

    @Test
    void testNestedBranchResolvesRecursively() {
        final StudyRequest sharedStep = new StudyRequest(name: 'shared', diagnosisCodes: ['K74.60'])
        final StudyRequest innerA = new StudyRequest(name: 'inner_a', diagnosisCodes: ['C22.0'])
        final StudyRequest innerB = new StudyRequest(name: 'inner_b', diagnosisCodes: ['C22.0'])
        final StudyRequest outerB = new StudyRequest(name: 'outer_b', diagnosisCodes: ['C22.0'])

        final SpecializedCohort cohort = new SpecializedCohort(
            name: 'nested',
            numPatients: 100,
            trajectory: [
                sharedStep,
                new BranchStep(branch: [
                    new BranchOption(
                        name: 'outer_a',
                        numPatients: 60,
                        steps: [
                            new BranchStep(branch: [
                                new BranchOption(name: 'inner_a', numPatients: 40, steps: [innerA]),
                                new BranchOption(name: 'inner_b', numPatients: 20, steps: [innerB])
                            ])
                        ]
                    ),
                    new BranchOption(name: 'outer_b', numPatients: 40, steps: [outerB])
                ])
            ]
        )

        final List<ResolvedCohort> resolved = cohort.resolve()
        assertEquals(resolved*.name.sort(), ['nested/outer_a/inner_a', 'nested/outer_a/inner_b', 'nested/outer_b'])
        assertEquals(resolved.sum { it.numPatients } as int, 100)
        resolved.each { r ->
            assertEquals(r.trajectory[0], sharedStep) // shared prefix preserved for every branch
        }
    }

}
