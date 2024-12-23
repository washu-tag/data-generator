package edu.washu.tag.generator.metadata.enums.contextGroups

import edu.washu.tag.generator.metadata.CodedTriplet

/**
 * From DICOM PS 3.16 CID 10
 */
enum InterventionalDrug {

    ETHANOL ('C-21047', 'SRT', 'Ethanol'),
    METHYLENE_BLUE ('C-22947', 'SRT', 'Methylene blue'),
    ANTIHISTAMINE ('C-51000', 'SRT', 'Antihistamine'),
    ATROPINE ('C-67770', 'SRT', 'Atropine'),
    DIURETIC ('C-72000', 'SRT', 'Diuretic'),
    ANTIARRHYTHMIC_DRUG ('C-80110', 'SRT', 'Antiarrhythmic drug'),
    INOTROPIC_AGENT ('C-80120', 'SRT', 'Inotropic agent'),
    CARDIOTONIC_DRUG ('C-80123', 'SRT', 'Cardiotonic drug'),
    CARDIAC_ADRENERGIC_BLOCKING_AGENT ('F-6181D', 'SRT', 'Cardiac adrenergic blocking agent'),
    ALPHA_ADRENERGIC_BLOCKING_AGENT ('C-80131', 'SRT', 'Alpha-adrenergic blocking agent'),
    BETA_ADRENERGIC_BLOCKING_AGENT ('C-80135', 'SRT', 'beta-Adrenergic blocking agent'),
    DIGOXIN ('C-80330', 'SRT', 'Digoxin'),
    LIDOCAINE ('C-80400', 'SRT', 'Lidocaine'),
    LIDOCAINE_HYDROCHLORIDE ('C-80401', 'SRT', 'Lidocaine hydrochloride'),
    NIFEDIPINE ('C-80430', 'SRT', 'Nifedipine'),
    PROPRANOLOL ('C-80450', 'SRT', 'Propranolol'),
    QUINIDINE ('C-80460', 'SRT', 'Quinidine'),
    VERAPAMIL ('C-80490', 'SRT', 'Verapamil'),
    HYPOTENSIVE_AGENT ('C-81100', 'SRT', 'Hypotensive agent'),
    CENTRALLY_ACTING_HYPOTENSIVE_AGENT ('C-81120', 'SRT', 'Centrally acting hypotensive agent'),
    NITROGLYCERIN ('C-81560', 'SRT', 'Nitroglycerin'),
    GLUCAGON_PREPARATION ('C-A2010', 'SRT', 'Glucagon preparation'),
    ANTICOAGULANT ('C-A6500', 'SRT', 'Anticoagulant'),
    WARFARIN ('C-A6530', 'SRT', 'Warfarin'),
    HEPARIN ('C-A6540', 'SRT', 'Heparin'),
    ANTI_HEPARIN_AGENT ('C-A6700', 'SRT', 'Anti-heparin agent'),
    PROTAMINE_SULFATE ('C-A6710', 'SRT', 'Protamine sulfate'),
    COAGULANT ('C-A6900', 'SRT', 'Coagulant'),
    HUMAN_FIBRINOGEN ('F-D7011', 'SRT', 'Human fibrinogen'),
    HEMOSTATIC_AGENT ('C-A7000', 'SRT', 'Hemostatic agent'),
    ASTRINGENT_DRUG ('C-A7001', 'SRT', 'Astringent drug'),
    ANTIHEMOPHILIC_FACTOR_PREPARATION ('C-A7021', 'SRT', 'Antihemophilic factor preparation'),
    THROMBIN_PREPARATION ('F-6ACA0', 'SRT', 'Thrombin preparation'),
    THROMBOPLASTIN ('F-D7B50', 'SRT', 'Thromboplastin'),
    DEXTRAN ('C-A7220', 'SRT', 'Dextran'),
    THROMBOLYTIC_AGENT ('C-50434', 'SRT', 'Thrombolytic agent'),
    STREPTOKINASE_PREPARATION ('C-A7420', 'SRT', 'Streptokinase preparation'),
    UROKINASE_PREPARATION ('C-A7430', 'SRT', 'Urokinase preparation'),
    INJECTABLE_FIBRINOLYSIN ('C-A7440', 'SRT', 'Injectable fibrinolysin'),
    TOLAZOLINE_HYDROCHOLORIDE ('C-815E1', 'SRT', 'Tolazoline hydrocholoride'),
    EPINEPHRINE ('F-B2135', 'SRT', 'Epinephrine')

    final CodedTriplet codedValue

    InterventionalDrug(String codeValue, String codingSchemeDesignator, String codeMeaning) {
        codedValue = new CodedTriplet(codeValue, codingSchemeDesignator, codeMeaning)
    }

}