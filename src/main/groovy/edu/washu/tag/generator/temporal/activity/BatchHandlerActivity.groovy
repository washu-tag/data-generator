package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.temporal.model.BatchHandlerActivityInput
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface BatchHandlerActivity {

    @ActivityMethod
    void formAndWriteBatch(BatchHandlerActivityInput batchHandlerActivityInput)

}
