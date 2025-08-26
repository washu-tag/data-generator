package edu.washu.tag.generator.hl7.v2

enum ReportVersion {

    V2_3 ('2.3'),
    V2_4 ('2.4'),
    V2_7 ('2.7')

    private String hl7Version

    ReportVersion(String version) {
        hl7Version = version
    }

    String getHl7Version() {
        hl7Version
    }

}