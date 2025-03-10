package edu.washu.tag.generator.temporal

import edu.washu.tag.generator.Hl7LogFile
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface FormHl7LogActivity {

    @WorkflowMethod
    void formLogFile(Hl7LogFile hl7LogFile)

}
