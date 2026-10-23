package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.cohorting.SpecializedCohort
import edu.washu.tag.generator.metadata.cohorting.StudyRequest
import edu.washu.tag.generator.metadata.protocols.MriSpecializedBrain
import edu.washu.tag.generator.metadata.protocols.SimpleCt
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class BatchingTest {

    @Test
    void testBatchingHappyPath() {
        final List<BatchRequest> batchRequests = new Batcher(
            new SpecificationParameters(
                numPatients: 4000,
                numStudies: 10000,
                numSeries: 30000
            )
        ).resolveBatches()
        assertEquals(batchRequests.size(), 2)
        batchRequests.each { request ->
            assertEquals(request.numPatients, 2000)
            assertEquals(request.numStudies, 5000)
            assertEquals(request.numSeries, 15000)
        }
        assertEquals(batchRequests[0].patientOffset, 0)
        assertEquals(batchRequests[0].studyOffset, 0)
        assertEquals(batchRequests[1].patientOffset, 2000)
        assertEquals(batchRequests[1].studyOffset, 5000)
    }

    @Test
    void testBatchingMessy() {
        final List<BatchRequest> batchRequests = new Batcher(
            new SpecificationParameters(
                numPatients: 4100,
                numStudies: 10000,
                numSeries: 30001
            )
        ).resolveBatches()
        assertEquals(batchRequests.size(), 3)
        final BatchRequest batch0 = batchRequests[0]
        assertEquals(batch0.numPatients, 1367)
        assertEquals(batch0.numStudies, 3334)
        assertEquals(batch0.numSeries, 10001)
        assertEquals(batch0.patientOffset, 0)
        assertEquals(batch0.studyOffset, 0)

        final BatchRequest batch1 = batchRequests[1]
        assertEquals(batch1.numPatients, 1367)
        assertEquals(batch1.numStudies, 3333)
        assertEquals(batch1.numSeries, 10000)
        assertEquals(batch1.patientOffset, 1367)
        assertEquals(batch1.studyOffset, 3334)

        final BatchRequest batch2 = batchRequests[2]
        assertEquals(batch2.numPatients, 1366)
        assertEquals(batch2.numStudies, 3333)
        assertEquals(batch2.numSeries, 10000)
        assertEquals(batch2.patientOffset, 2734)
        assertEquals(batch2.studyOffset, 6667)
    }

    @Test
    void testBatchingWithCohorts() {
        final List<BatchRequest> batchRequests = new Batcher(
            new SpecificationParameters(
                numPatients: 1000,
                numStudies: 10000,
                numSeries: 30000,
                cohorts: [
                    new SpecializedCohort(
                        name: 'cohort1',
                        numPatients: 4000,
                        trajectory: [
                            new StudyRequest(
                                name: 'MR',
                                protocol: new MriSpecializedBrain()
                            ),
                            new StudyRequest(
                                name: 'CT',
                                protocol: new SimpleCt()
                            )
                        ]
                    ),
                    new SpecializedCohort(
                        name: 'cohort2',
                        numPatients: 2000,
                        trajectory: [
                            new StudyRequest(
                                name: 'MR',
                                protocol: new MriSpecializedBrain()
                            ),
                            new StudyRequest(
                                name: 'CT',
                                protocol: new SimpleCt()
                            ),
                            new StudyRequest(
                                name: 'CT2',
                                protocol: new SimpleCt()
                            )
                        ]
                    )
                ]
            )
        ).resolveBatches()
        assertEquals(batchRequests.size(), 4)
        final BatchRequest batch0 = batchRequests[0]
        assertEquals(batch0.numPatients, 1000)
        assertEquals(batch0.numStudies, 10000)
        assertEquals(batch0.numSeries, 30000)
        assertEquals(batch0.patientOffset, 0)
        assertEquals(batch0.studyOffset, 0)

        final BatchRequest batch1 = batchRequests[1]
        assertEquals(batch1.numPatients, 2000)
        assertEquals(batch1.numStudies, 4000)
        assertEquals(batch1.patientOffset, 1000)
        assertEquals(batch1.studyOffset, 10000)
        assertEquals(batch1.cohortName, 'cohort1')

        final BatchRequest batch2 = batchRequests[2]
        assertEquals(batch2.numPatients, 2000)
        assertEquals(batch2.numStudies, 4000)
        assertEquals(batch2.patientOffset, 3000)
        assertEquals(batch2.studyOffset, 14000)
        assertEquals(batch2.cohortName, 'cohort1')

        final BatchRequest batch3 = batchRequests[3]
        assertEquals(batch3.numPatients, 2000)
        assertEquals(batch3.numStudies, 6000)
        assertEquals(batch3.patientOffset, 5000)
        assertEquals(batch3.studyOffset, 18000)
        assertEquals(batch3.cohortName, 'cohort2')
    }

    // A multi-study-per-patient cohort whose patient count doesn't divide evenly across the batches it
    // lands in: numStudies per batch must equal that batch's own numPatients * trajectory size, not an
    // independently-proportioned share of the cohort's total study count.
    @Test
    void testBatchingCohortWithUnevenPatientSplit() {
        final int trajectorySize = 7
        final List<BatchRequest> batchRequests = new Batcher(
            new SpecificationParameters(
                cohorts: [
                    new SpecializedCohort(
                        name: 'cohort1',
                        numPatients: 50,
                        trajectory: (1..trajectorySize).collect { i ->
                            new StudyRequest(name: "step${i}", protocol: new SimpleCt())
                        }
                    )
                ]
            ),
            20
        ).resolveBatches()

        assertEquals(batchRequests.size(), 3)
        assertEquals(batchRequests*.numPatients, [17, 17, 16])
        batchRequests.each { batch ->
            assertEquals(batch.numStudies, batch.numPatients * trajectorySize, "batch ${batch.id}")
        }

        // The real-world symptom: each batch draws studyIds from its own SequentialIdGenerator, one per
        // actual study generated (numPatients * trajectorySize), regardless of what numStudies says it was
        // allocated. If a batch's real draw count exceeds its allocation, it runs into the next batch's
        // starting offset and both end up handing out the same studyId - and therefore the same
        // AccessionNumber - to two different patients.
        final List<Integer> allStudyIds = batchRequests.collectMany { batch ->
            final int realStudyCount = batch.numPatients * trajectorySize
            (1..realStudyCount).collect { batch.studyOffset + it }
        }
        assertEquals(allStudyIds.size(), allStudyIds.toSet().size(), "duplicate studyIds across batches: ${allStudyIds}")
    }

}
