package edu.washu.tag.generator.ai

import com.fasterxml.jackson.annotation.JsonPropertyDescription

class GeneratedReport {

    @JsonPropertyDescription('UID for imaging study')
    String uid
    @JsonPropertyDescription('Brief description of imaging exam performed, not mentioning date')
    String examination
    @JsonPropertyDescription('Detailed and lengthy narrative description of images')
    String findings
    @JsonPropertyDescription("Radiologist's detailed conclusions based on findings")
    String impressions

}
