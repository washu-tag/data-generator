package edu.washu.tag.generator.metadata.pixels

import edu.washu.tag.generator.metadata.Series
import edu.washu.tag.generator.metadata.Study
import org.dcm4che3.data.Attributes
import org.dcm4che3.data.Tag
import org.dcm4che3.data.UID
import org.dcm4che3.data.VR
import org.dcm4che3.image.BufferedImageUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.time.format.DateTimeFormatter

class DerivedCtDoseReport implements PixelSpecification, PixelSource {

    public static final DerivedCtDoseReport INSTANCE = new DerivedCtDoseReport()
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern('dd-LLL-uuuu  HH:mm')
    private static final Logger logger = LoggerFactory.getLogger(DerivedCtDoseReport)

    @Override
    PixelSource generateSource() {
        this
    }

    @Override
    Attributes produceImage(Study study, Series doseReport) {
        final BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_BYTE_GRAY)
        final Graphics2D graphics2D = image.createGraphics()
        graphics2D.setColor(Color.BLACK)
        graphics2D.fillRect(0, 0, 512, 512)

        graphics2D.setFont(new Font('Verdana', Font.PLAIN, 9))
        graphics2D.setColor(Color.WHITE)
        try {
            graphics2D.drawString(timeFormatter.format(study.studyDateTime()), 10, 10)
            graphics2D.drawString('Ward:', 10, 30)
            graphics2D.drawString('Physician:', 10, 40)
            final String performing = doseReport.performingPhysiciansName?[0]
            if (performing != null) {
                graphics2D.drawString(performing.toUpperCase(), 115, 40)
            }
            graphics2D.drawString('Operator:', 10, 50)
            final String operator = doseReport.operatorsName?[0]
            if (operator != null) {
                graphics2D.drawString(operator.toUpperCase(), 115, 50)
            }
            graphics2D.drawString('Total mAs 5990', 10, 70)
            graphics2D.drawString('Total DLP 492 mGycm', 95, 70)
            graphics2D.drawString('Scan', 130, 90)
            graphics2D.drawString('kV', 200, 90)
            graphics2D.drawString('mAs  / ref.', 230, 90)
            graphics2D.drawString('CTDIvol*', 300, 90)
            graphics2D.drawString('DLP', 380, 90)
            graphics2D.drawString('TI', 420, 90)
            graphics2D.drawString('cSL', 450, 90)
            graphics2D.drawString('mGy', 319, 100)
            graphics2D.drawString('mGycm', 363, 100)
            graphics2D.drawString('Patient Position H-SP', 10, 120)

            final List<BurnedInSeries> burnedInSeries = []
            study.series.findAll { series ->
                series.modality == 'CT' && series.seriesInstanceUid != doseReport.seriesInstanceUid
            }.sort {
                it.seriesNumber
            }.each { series ->
                burnedInSeries << new BurnedInSeries(series.seriesDescription, series.seriesNumber)
            }
            study.series.findAll { series ->
                series.modality == 'PT'
            }.each { series ->
                burnedInSeries << new BurnedInSeries(series.seriesDescription, '')
            }

            burnedInSeries.eachWithIndex { series, index ->
                final verticalCoordinate = 130 + 10 * index
                graphics2D.drawString(series.description(), 10, verticalCoordinate)
                graphics2D.drawString(series.number(), 135, verticalCoordinate)
            }
        } catch (RuntimeException exception) {
            logger.warn('Failed to draw string in graphics object of CT Dose report', exception)
        }

        graphics2D.dispose()
        final Attributes attributes = BufferedImageUtils.toImagePixelModule(image, null)
        attributes.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian)
        attributes
    }

    private static record BurnedInSeries (String description, String number) {

    }

}
