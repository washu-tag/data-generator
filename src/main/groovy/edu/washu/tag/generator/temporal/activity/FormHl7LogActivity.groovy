package edu.washu.tag.generator.temporal.activity

import edu.washu.tag.generator.Hl7LogFile
import edu.washu.tag.generator.temporal.model.FormHl7LogFileInput
import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FormHl7LogActivity {

    @ActivityMethod
    Map<String, List<Hl7LogFile>> identifyLogFiles(File baseDir)

    @ActivityMethod
    void formLogFile(FormHl7LogFileInput input)

}
