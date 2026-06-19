package edu.washu.tag.generator.metadata.module.study

import org.apache.commons.math3.distribution.EnumeratedDistribution
import org.dcm4che3.util.UIDUtils
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.Equipment
import edu.washu.tag.generator.metadata.GenerationCache
import edu.washu.tag.generator.metadata.Patient
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.Protocol
import edu.washu.tag.generator.metadata.Study
import edu.washu.tag.generator.metadata.module.StudyLevelModule
import edu.washu.tag.generator.util.RandomGenUtils

class GeneralStudyModule implements StudyLevelModule {

    private static final EnumeratedDistribution<Closure<String>> operatorNameFunctionRandomizer = RandomGenUtils.setupWeightedLottery([
            ({ Person person, boolean supportsNonLatin -> person.serializeToInitials() })             : 60,
            ({ Person person, boolean supportsNonLatin -> person.serializeToDicom(supportsNonLatin) }): 40
    ]) as EnumeratedDistribution<Closure>

    // Study ID and Accession Number handled outside
    @Override
    void apply(SpecificationParameters specificationParameters, Patient patient, Study study) {
        final Protocol protocol = study.protocol
        final Equipment scanner = study.primaryEquipment // if more than one device, they should at least handle all study-level fields the same
        study.setStudyInstanceUid(UIDUtils.createUID())
        study.setStudyDate(specificationParameters.studyDateDistribution.generateStudyDate(patient))
        study.setStudyTime(RandomGenUtils.randomStudyTime())

        if (protocol.includeMedicalStaff()) {
            if (RandomGenUtils.weightedCoinFlip(60)) {
                study.setReferringPhysicianName(scanner.serializeName(GenerationCache.selectPhysician(patient.nationality)))
            }
            if (RandomGenUtils.weightedCoinFlip(10)) {
                final int numPhysicians = RandomGenUtils.weightedCoinFlip(90) ? 1 : 2
                study.setConsultingPhysicianName(
                        GenerationCache.selectPhysicians(patient.nationality, numPhysicians).collect { physician ->
                            physician.serializeToDicom(scanner.supportsNonLatinCharacterSets())
                        }
                )
            }
            final Map<Equipment, List<String>> operatorMap = [:]
            study.equipmentMap.values().unique(false).each { equipment ->
                final int numOperators = RandomGenUtils.weightedCoinFlip(70) ? 1 : 2
                final List<Person> operators = GenerationCache.selectOperators(equipment.institution, numOperators)
                if (equipment == scanner) {
                    study.setPrimaryOperators(operators) // we want this always for reports
                }
                if (RandomGenUtils.weightedCoinFlip(95)) {
                    final Closure<String> operatorSerializer = operatorNameFunctionRandomizer.sample()
                    final List<String> operatorNames = operators.collect { operator ->
                        operatorSerializer(operator, equipment.supportsNonLatinCharacterSets()) // belongs to C.7.3.1 General Series Module
                    }
                    operatorMap.put(equipment, operatorNames)
                }
            }
            study.setOperatorMap(operatorMap)
            if (RandomGenUtils.weightedCoinFlip(95)) {
                final int numPerforming = RandomGenUtils.weightedCoinFlip(90) ? 1 : 2
                study.setPerformingPhysiciansName(GenerationCache.selectPhysicians(scanner.institution, numPerforming).collect { physician ->
                    physician.serializeToDicom(scanner.supportsNonLatinCharacterSets()) // belongs to C.7.3.1 General Series Module
                })
            }
        }

        study.setStudyDescription(protocol.getStudyDescription(scanner, study))
    }

}
