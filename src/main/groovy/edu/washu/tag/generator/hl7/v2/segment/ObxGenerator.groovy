package edu.washu.tag.generator.hl7.v2.segment

import ca.uhn.hl7v2.model.Type
import ca.uhn.hl7v2.model.v281.datatype.ST
import ca.uhn.hl7v2.model.v281.segment.OBX
import ca.uhn.hl7v2.util.Terser
import edu.washu.tag.generator.hl7.v2.model.ReportStatus
import edu.washu.tag.generator.metadata.Person
import edu.washu.tag.generator.metadata.RadiologyReport
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
class ObxGenerator extends SegmentGenerator<OBX> {

    String setId
    Obx5Provider content
    String observationId
    String observationSubId

    ObxGenerator() {

    }

    static ObxGenerator forGeneralDescription(String content) {
        new ObxGenerator()
                .content(content)
                .observationId('GDT')
                .observationSubId('1')
    }

    static ObxGenerator forImpression(String content) {
        new ObxGenerator()
                .content(content)
                .observationId('IMP')
                .observationSubId('2')
    }

    ObxGenerator content(String contentVal) {
        content(new SingleValueObx5Provider(contentVal) {
            @Override
            Type resolveType(OBX baseSegment) {
                final ST st = new ST(baseSegment.getMessage())
                st.setValue(contentVal)
                st
            }
        })
    }

    @Override
    void generateSegment(RadiologyReport radReport, OBX baseSegment) {
        baseSegment.getObx1_SetIDOBX().setValue(setId)
        baseSegment.getObx2_ValueType().setValue(content.getValueType(baseSegment))
        encodeObservationId(baseSegment)
        baseSegment.getObx4_ObservationSubID().setValue(observationSubId)
        content.encodeObx5(baseSegment)
        baseSegment.getObx11_ObservationResultStatus().setValue(getEncodedStatus(radReport.orcStatus))
        if (radReport.technician != null) {
            encodeResponsibleObserver(radReport.technician, baseSegment)
        }
    }

    protected void encodeObservationId(OBX baseSegment) {
        Terser.set(baseSegment, 3, 0, 1, 2, observationId)
    }

    protected String getEncodedStatus(ReportStatus status) {
        return status.currentText
    }

    protected void encodeResponsibleObserver(Person technician, OBX baseSegment) {
        technician.toXcn(baseSegment.getObx16_ResponsibleObserver(0), true)
    }

    abstract class Obx5Provider {
        abstract String getValueType(OBX baseSegment)

        abstract void encodeObx5(OBX baseSegment)
    }

    protected abstract class SingleValueObx5Provider extends Obx5Provider {
        protected final String content
        private Type resolvedType

        SingleValueObx5Provider(String content) {
            this.content = content
        }

        private void ensureResolved(OBX baseSegment) {
            if (resolvedType == null) {
                resolvedType = resolveType(baseSegment)
            }
        }

        abstract Type resolveType(OBX baseSegment)

        @Override
        String getValueType(OBX baseSegment) {
            ensureResolved(baseSegment)
            resolvedType.class.simpleName
        }

        @Override
        void encodeObx5(OBX baseSegment) {
            baseSegment.getObx5_ObservationValue(0).setData(resolvedType)
        }
    }


}
