package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.Hl7LogFile
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FormHl7LogActivity {

    @ActivityMethod
    List<Hl7LogFile> identifyLogFiles(File baseDir)

    @ActivityMethod
    void formLogFile(Hl7LogFile hl7LogFile)

}
