package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.ManifestMerger
import edu.washu.tag.generator.temporal.TemporalApplication
import io.temporal.spring.boot.ActivityImpl
import io.temporal.workflow.Workflow
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
@ActivityImpl(taskQueues = TemporalApplication.TASK_QUEUE)
class FormManifestActivityImpl implements FormManifestActivity {

    private static final Logger logger = Workflow.getLogger(FormManifestActivityImpl)

    @Override
    void combineManifests(File hl7BaseDir) {
        logger.info("Combining per-batch manifests under ${hl7BaseDir}")
        ManifestMerger.combineManifests(hl7BaseDir)
    }

}
