package edu.washu.tag.generator.temporal

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TemporalApplication {

    static final String PARENT_QUEUE = 'data-generator'
    static final String CHILD_QUEUE = 'data-generator-delegate'

    static void main(String[] args) {
        SpringApplication.run(TemporalApplication, args)
    }

}