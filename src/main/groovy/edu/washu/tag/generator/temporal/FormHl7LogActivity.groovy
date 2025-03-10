package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.Hl7LogFile
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FormHl7LogActivity {

    @ActivityMethod
    void formLogFile(Hl7LogFile hl7LogFile)

}
