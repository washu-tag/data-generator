package edu.washu.tag.generator

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.washu.tag.QuerySourceData
import edu.washu.tag.generator.metadata.Patient

class BatchSpecification implements QuerySourceData {

    int id
    List<Patient> patients = []
    @JsonIgnore written = false
    public static final int MAX_PATIENTS = 2000
    public static final int MAX_PATIENTS_PER_LAYER = 100

    @JsonIgnore
    boolean isFull() {
        patients.size() > MAX_PATIENTS // TODO: what if a few patients have many, many, many studies?
    }

    String relativeFilePath() {
        "batches/batch_${id}.yaml"
    }

    File asFile() {
        new File(relativeFilePath())
    }

    void writeToFile() {
        new YamlObjectMapper().writeValue(asFile(), this)
        written = true
    }

    void generateDicom(int index, int totalNumBatches) {
        println("Writing batch with id ${id} to DICOM...")
        final OutputHandler outputHandler = new OutputHandler(id)
        patients.each { patient ->
            outputHandler.writeDicomForPatient(patient)
        }
        println("Successfully wrote batch with id ${id} to DICOM [${index + 1}/${totalNumBatches}]")
    }

    void generateHl7(int index, int totalNumBatches) {
        println("Writing batch with id ${id} to HL7 v2...")
        final OutputHandler outputHandler = new OutputHandler(id)
        patients.each { patient ->
            outputHandler.writeHl7ForPatient(patient)
        }

        println("Successfully wrote batch with id ${id} to HL7 [${index + 1}/${totalNumBatches}]")
    }

    boolean containsRadiologyReport() {
        patients.any { patient ->
            patient.studies.any { study ->
                study.radReport != null
            }
        }
    }

}
