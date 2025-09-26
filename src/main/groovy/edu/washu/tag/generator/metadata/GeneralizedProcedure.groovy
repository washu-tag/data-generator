package edu.washu.tag.generator.metadata

enum GeneralizedProcedure {

    XR (10),
    CT (11),
    MRI (12),
    NM (13),
    US (14),
    DXA (16),
    MAMMO (17),
    IR (18),
    FLUOROSCOPY (20),
    PET (24)

    final String code
    final String meaning

    GeneralizedProcedure(int code) {
        this.code = String.valueOf(code)
        meaning = "IMG ${name()} PROCEDURES"
    }

}