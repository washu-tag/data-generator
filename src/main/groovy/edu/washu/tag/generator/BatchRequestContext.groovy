package edu.washu.tag.generator

import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.patient.PatientRandomizer
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import org.apache.commons.math3.distribution.EnumeratedDistribution

import java.util.function.Consumer

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class BatchRequestContext {

    SpecificationParameters specificationParameters
    GenerationCache generationCache
    IdOffsets idOffsets
    EnumeratedDistribution<PatientRandomizer> patientRandomizers
    Consumer<Patient> handler
    boolean generateReports = true
    boolean temporalHeartbeat = false

}
