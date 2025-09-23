package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.AbstractGroup
import ca.uhn.hl7v2.model.v281.datatype.CWE
import ca.uhn.hl7v2.model.v281.message.ORU_R01
import ca.uhn.hl7v2.model.v281.segment.DG1
import edu.washu.tag.generator.ai.catalog.attribute.DiagnosisCode
import edu.washu.tag.generator.metadata.RadiologyReport

class Dg1Generator extends NonstandardSegmentGenerator<DG1> {

    /*
      Observations from real data to explain the choices made here:

      * DG1-1 looks like an index for the segments, starting at 1
      * DG1-2 contains the coding scheme, either I9 or I10
      * DG1-3 contains a coded value with 3 components: the code, code meaning, and the coding scheme from DG1-2 again
      * DG1-4 contains the code meaning repeated
      * DG1-6 contains only a second component, but it's a small string that's always the same. It does not seem
        useful so I will leave it out for now.
     */

    int index
    DiagnosisCode code
    String designator

    Dg1Generator(int index, DiagnosisCode code, String designator) {
        this.index = index
        this.code = code
        this.designator = designator
    }

    @Override
    String getSegmentName() {
        'DG1'
    }

    @Override
    AbstractGroup positionForSegment(ORU_R01 baseMessage) {
        baseMessage.PATIENT_RESULT.ORDER_OBSERVATION
    }

    @Override
    int insertionIndex(AbstractGroup position) {
        3
    }

    @Override
    int repetition() {
        index
    }

    @Override
    void generateSegment(RadiologyReport radReport, DG1 baseSegment) {
        baseSegment.getDg11_SetIDDG1().setValue(String.valueOf(index + 1))
        baseSegment.getDg12_DiagnosisCodingMethod().setValue(designator)
        final CWE codeCwe = baseSegment.getDg13_DiagnosisCodeDG1()
        codeCwe.getCwe1_Identifier().setValue(code.code)
        codeCwe.getCwe2_Text().setValue(code.codeMeaning)
        codeCwe.getCwe3_NameOfCodingSystem().setValue(designator)
        baseSegment.getDg14_DiagnosisDescription().setValue(code.codeMeaning)
    }

}
