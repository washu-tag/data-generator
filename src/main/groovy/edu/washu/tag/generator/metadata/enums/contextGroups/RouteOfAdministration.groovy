package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 11
 */
enum RouteOfAdministration {

    INTRAVENOUS_ROUTE ('G-D101', 'SRT', 'Intravenous route'),
    INTRA_ARTERIAL_ROUTE ('G-D102', 'SRT', 'Intra-arterial route'),
    INTRAMUSCULAR_ROUTE ('G-D103', 'SRT', 'Intramuscular route'),
    SUBCUTANEOUS_ROUTE ('G-D104', 'SRT', 'Subcutaneous route'),
    INTRACUTANEOUS_ROUTE ('G-D17D', 'SRT', 'Intracutaneous route'),
    INTRAPERITONEAL_ROUTE ('G-D106', 'SRT', 'Intraperitoneal route'),
    INTRAMEDULLARY_ROUTE ('G-D107', 'SRT', 'Intramedullary route'),
    INTRATHECAL_ROUTE ('G-D108', 'SRT', 'Intrathecal route'),
    INTRA_ARTICULAR_ROUTE ('G-D109', 'SRT', 'Intra-articular route'),
    INTRAEPITHELIAL_ROUTE ('C38244', 'NCIt', 'Intraepithelial route'),
    TOPICAL_ROUTE ('G-D112', 'SRT', 'Topical route'),
    ORAL_ROUTE ('G-D140', 'SRT', 'Oral route'),
    TRANSLUMINAL_ROUTE ('C38306', 'NCIt', 'Transluminal route'),
    INTRALUMINAL_ROUTE ('G-D144', 'SRT', 'Intraluminal route'),
    EXTRALUMINAL_ROUTE ('C38213', 'NCIt', 'Extraluminal route'),
    BY_INHALATION ('R-40B32', 'SRT', 'By inhalation'),
    PER_RECTUM ('G-D160', 'SRT', 'Per rectum'),
    VAGINAL_ROUTE ('G-D164', 'SRT', 'Vaginal route'),
    INTRACORONARY_ROUTE ('G-D17C', 'SRT', 'Intracoronary route'),
    INTRACARDIAC_ROUTE ('G-D173', 'SRT', 'Intracardiac route'),
    INTRAVENTRICULAR_ROUTE_CARDIAC ('R-F2C86', 'SRT', 'Intraventricular route - cardiac'),
    RETRO_ORBITAL_ROUTE ('127070', 'DCM', 'Retro-orbital route'),
    NASAL_ROUTE ('G-D172', 'SRT', 'Nasal route'),
    INTRADERMAL_ROUTE ('G-D17D', 'SRT', 'Intradermal route'),
    INTRATUMOR_ROUTE ('R-F2CD4', 'SRT', 'Intratumor route')

    final CodedTriplet codedValue

    RouteOfAdministration(String codeValue, String codingSchemeDesignator, String codeMeaning) {
        codedValue = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
    }

}