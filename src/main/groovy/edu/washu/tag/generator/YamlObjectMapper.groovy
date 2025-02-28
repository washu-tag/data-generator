package edu.washu.tag.generator

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.yaml.snakeyaml.LoaderOptions

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class YamlObjectMapper extends ObjectMapper {

    YamlObjectMapper() {
        super(constructYamlFactory())
        enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
        enable(JsonParser.Feature.ALLOW_YAML_COMMENTS)
        enable(SerializationFeature.INDENT_OUTPUT)
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        final JavaTimeModule javaTimeModule = new JavaTimeModule()
        final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern('uuuu-MM-dd')
        final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern('HH:mm:ss')
        javaTimeModule.addDeserializer(LocalDate, new LocalDateDeserializer(dateFormat))
        javaTimeModule.addSerializer(LocalDate, new LocalDateSerializer(dateFormat))
        javaTimeModule.addDeserializer(LocalTime, new LocalTimeDeserializer(timeFormat))
        javaTimeModule.addSerializer(LocalTime, new LocalTimeSerializer(timeFormat))
        registerModule(javaTimeModule)
    }

    private static YAMLFactory constructYamlFactory() {
        final LoaderOptions loaderOptions = new LoaderOptions()
        loaderOptions.setCodePointLimit(300 * 1024 * 1024)
        YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .disable(YAMLGenerator.Feature.SPLIT_LINES)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .loaderOptions(loaderOptions)
                .build()
    }

}

