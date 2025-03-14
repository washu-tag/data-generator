package edu.washu.tag.generator.metadata

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import edu.washu.tag.generator.SpecificationParameters
import edu.washu.tag.generator.metadata.enums.BodyPart
import edu.washu.tag.generator.metadata.enums.SeriesBundling
import edu.washu.tag.generator.metadata.module.StudyLevelModule
import edu.washu.tag.generator.metadata.module.study.GeneralStudyModule
import edu.washu.tag.generator.metadata.module.study.PatientStudyModule
import edu.washu.tag.generator.metadata.protocols.*
import edu.washu.tag.generator.util.RandomGenUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = 'type'
)
@JsonSubTypes([
        @JsonSubTypes.Type(value = MriComplexProtocol, name = 'mri_full'),
        @JsonSubTypes.Type(value = MriMinimalProtocol, name = 'mri_min'),
        @JsonSubTypes.Type(value = MriSpecializedBrain, name = 'mri_specialized_brain'),
        @JsonSubTypes.Type(value = OutsideMri, name = 'mri_outside'),
        @JsonSubTypes.Type(value = MammogramSingleView, name = 'mg'),
        @JsonSubTypes.Type(value = MammogramFourView, name = 'mg4view'),
        @JsonSubTypes.Type(value = PetCt, name = 'petct'),
        @JsonSubTypes.Type(value = AnteroposteriorCr, name = 'ap_cr'),
        @JsonSubTypes.Type(value = SingleViewXRay, name = 'dx'),
        @JsonSubTypes.Type(value = MyocardialPerfusion, name = 'nm_mpi'),
        @JsonSubTypes.Type(value = OutsideMyocardialPerfusion, name = 'outside_nm_mpi'),
        @JsonSubTypes.Type(value = Ultrasound, name = 'us'),
        @JsonSubTypes.Type(value = XRayAngiography, name = 'xa'),
        @JsonSubTypes.Type(value = OutsideXRayAngiography, name = 'outside_xa'),
        @JsonSubTypes.Type(value = PetMr, name = 'petmr'),
        @JsonSubTypes.Type(value = JapaneseInstitutionXRay, name = 'japanese_dx'),
        @JsonSubTypes.Type(value = KoreanInstitutionXRay, name = 'korean_dx'),
        @JsonSubTypes.Type(value = SecondaryCaptureXRayStudy, name = 'sc_xr'),
        @JsonSubTypes.Type(value = SecondaryCaptureMammogramStudy, name = 'sc_mg'),
        @JsonSubTypes.Type(value = GreekMri, name = 'greek_mri'),
        @JsonSubTypes.Type(value = ContouredMriWithKo, name = 'mri_rt_ko'),
        @JsonSubTypes.Type(value = MammogramWithAnnotations, name = 'mg_pr'),
        @JsonSubTypes.Type(value = PetCtGemini, name = 'petct2')
])
abstract class Protocol implements Randomizeable {

    List<Equipment> scanners = []
    List<SeriesType> seriesTypes
    List<BodyPart> bodyParts
    private static final GeneralStudyModule generalStudyModule = new GeneralStudyModule()
    private static final PatientStudyModule patientStudyModule = new PatientStudyModule()

    Protocol() {
        seriesTypes = getAllSeriesTypes()
        seriesTypes.each { seriesType ->
            seriesType.compatibleEquipment.each { scanner ->
                if (!scanners.any {
                    it.class == scanner
                }) {
                    scanners << scanner.newInstance()
                }
            }
        }
        bodyParts = getApplicableBodyParts()
    }

    abstract List<SeriesType> getAllSeriesTypes()

    abstract List<BodyPart> getApplicableBodyParts()

    abstract String getStudyDescription(Equipment scanner, Study study)

    abstract String getSimpleDescription(BodyPart bodyPart)

    abstract ProcedureCode getProcedureCode(BodyPart bodyPart)

    boolean isApplicableFor(Patient patient) {
        true
    }

    void resample(Patient patient) {}

    boolean isXnatCompatible() {
        true
    }

    boolean includeMedicalStaff() {
        true
    }

    boolean allowPatientPhysiqueEncoding() {
        true
    }

    List<StudyLevelModule> additionalStudyModules() {
        []
    }

    final Study setFieldsFor(SpecificationParameters specificationParameters, Patient patient, Study study) {
        resample(patient)
        final BodyPart bodyPartExamined = bodyParts ? RandomGenUtils.randomListEntry(bodyParts) : null
        study.setSimpleDescription(getSimpleDescription(bodyPartExamined))
        study.setProcedureCodeId(getProcedureCode(bodyPartExamined)?.id)
        Equipment primaryScanner = null
        seriesTypes*.seriesBundling().unique().each { bundling ->
            final List<SeriesType> seriesTypesForBundle = seriesTypes.findAll { seriesType ->
                seriesType.seriesBundling() == bundling
            }
            final Equipment scanner = RandomGenUtils.randomListEntry(scanners.findAll { scanner ->
                !seriesTypesForBundle.any { seriesType ->
                    !seriesType.compatibleEquipment.contains(scanner.class)
                }
            })
            scanner.resample()
            if (bundling == SeriesBundling.PRIMARY) {
                primaryScanner = scanner
                study.setPrimaryEquipment(scanner)
                study.dateOffsetMap.put(bundling, 0)
            } else {
                study.dateOffsetMap.put(bundling, ThreadLocalRandom.current().nextInt(bundling.minDaysPassed, bundling.maxDaysPassed))
                study.seriesTimeMap.put(bundling, RandomGenUtils.randomStudyTime())
            }
            study.equipmentMap.put(bundling, scanner)
        }
        study.setBodyPartExamined(bodyPartExamined)
        final Person person = patient.patientName
        study.setPatientName(primaryScanner.serializeName(person))
        if (!person.requiredCharacterSets.isEmpty() && primaryScanner.supportsNonLatinCharacterSets()) {
            study.setCharacterSets(person.requiredCharacterSets)
        }
        ([generalStudyModule, patientStudyModule] + additionalStudyModules()).each { module ->
            (module as StudyLevelModule).apply(specificationParameters, patient, study)
        }
        seriesTypes.eachWithIndex { seriesType, seriesIndex ->
            study.series << seriesType
                .seriesClass()
                .getDeclaredConstructor()
                .newInstance()
                .randomize(specificationParameters, seriesType, study, seriesIndex)
        }
        study.series.each { series ->
            series.generateInstances(study) // done after to allow series to see all other series in study
        }
        study
    }

}