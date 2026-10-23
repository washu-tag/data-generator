package edu.washu.tag.generator.temporal.activity

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface FormManifestActivity {

    @ActivityMethod
    void combineManifests(File hl7BaseDir)

}
