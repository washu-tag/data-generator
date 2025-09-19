package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.HL7Exception
import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.v281.datatype.RP
import ca.uhn.hl7v2.model.v281.segment.OBX
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.util.LineWrapper

class ObxGeneratorHistorical extends ObxGenerator {

    ObxGeneratorHistorical(String content, boolean isTextSegment = true) {
        if (isTextSegment) {
            setContent(new Obx5Provider() {
                @Override
                String getValueType(OBX baseSegment) {
                    'TX'
                }

                @Override
                void encodeObx5(OBX baseSegment) throws HL7Exception {
                    content
                            .replace('\n', '~')
                            .split('~')
                            .collect { LineWrapper.splitLongLines(it) }
                            .collectMany { it }
                            .eachWithIndex { line, i ->
                                Terser.set(baseSegment, 5, i, 1, 1, line)
                            }
                }
            })
        } else {
            setContent(new SingleValueObx5Provider(content) {
                @Override
                Type resolveType(OBX baseSegment) {
                    final RP obx5 = new RP(baseSegment.getMessage())
                    obx5.getRp1_Pointer().setValue(content)
                    obx5.getRp2_ApplicationID().getHd1_NamespaceID().setValue('UK')
                    obx5.getRp3_TypeOfData().setValue('ZB')
                    obx5
                }
            })
        }
    }

    @Override
    String getEncodedStatus(ReportStatus status) {
        status.historicalText
    }

    @Override
    protected void encodeResponsibleObserver(Person technician, OBX baseSegment) {

    }

}
