package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 4020
 */
enum PetRadionuclide {

    _11_CARBON ('C-105A1', 'SRT', '^11^Carbon', '1223', '0.97'),
    _13_NITROGEN ('C-107A1', 'SRT', '^13^Nitrogen'),
    _14_OXYGEN ('C-1018C', 'SRT', '^14^Oxygen'),
    _15_OXYGEN ('C-B1038', 'SRT', '^15^Oxygen'),
    _18_FLUORINE ('C-111A1', 'SRT', '^18^Fluorine', '6586.2', '0.97'),
    _22_SODIUM ('C-155A1', 'SRT', '^22^Sodium'),
    _38_POTASSIUM ('C-135A4', 'SRT', '^38^Potassium'),
    _43_SCANDIUM ('126605', 'DCM', '^43^Scandium'),
    _44_SCANDIUM ('126600', 'DCM', '^44^Scandium'),
    _45_TITANIUM ('C-166A2', 'SRT', '^45^Titanium'),
    _51_MANGANESE ('126601', 'DCM', '^51^Manganese'),
    _52_IRON ('C-130A1', 'SRT', '^52^Iron'),
    _52_MANGANESE ('C-149A1', 'SRT', '^52^Manganese'),
    _52M_MANGANESE ('126607', 'DCM', '^52m^Manganese'),
    _60_COPPER ('C-127A4', 'SRT', '^60^Copper'),
    _61_COPPER ('C-127A1', 'SRT', '^61^Copper'),
    _62_COPPER ('C-127A5', 'SRT', '^62^Copper'),
    _62_ZINC ('C-141A1', 'SRT', '^62^Zinc'),
    _64_COPPER ('C-127A2', 'SRT', '^64^Copper'),
    _66_GALLIUM ('C-131A1', 'SRT', '^66^Gallium'),
    _68_GALLIUM ('C-131A3', 'SRT', '^68^Gallium'),
    _68_GERMANIUM ('C-128A2', 'SRT', '^68^Germanium'),
    _70_ARSENIC ('126602', 'DCM', '^70^Arsenic'),
    _72_ARSENIC ('C-115A2', 'SRT', '^72^Arsenic'),
    _73_SELENIUM ('C-116A2', 'SRT', '^73^Selenium'),
    _75_BROMINE ('C-113A1', 'SRT', '^75^Bromine'),
    _76_BROMINE ('C-113A2', 'SRT', '^76^Bromine'),
    _77_BROMINE ('C-113A3', 'SRT', '^77^Bromine'),
    _82_RUBIDIUM ('C-159A2', 'SRT', '^82^Rubidium'),
    _86_YTTRIUM ('C-162A3', 'SRT', '^86^Yttrium'),
    _89_ZIRCONIUM ('C-168A4', 'SRT', '^89^Zirconium'),
    _90_NIOBIUM ('126603', 'DCM', '^90^Niobium'),
    _90_YTTRIUM ('C-162A7', 'SRT', '^90^Yttrium'),
    _94M_TECHNETIUM ('C-163AA', 'SRT', '^94m^Technetium'),
    _124_IODINE ('C-114A5', 'SRT', '^124^Iodine'),
    _152_TERBIUM ('126606', 'DCM', '^152^Terbium')

    final CodedTriplet codedValue
    final String halfLife
    final String positronFraction

    PetRadionuclide(String codeValue, String codingSchemeDesignator, String codeMeaning, String isotopeHalfLife = null, String isotropePositronFraction = null) {
        codedValue = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
        halfLife = isotopeHalfLife
        positronFraction = isotropePositronFraction
    }

}
