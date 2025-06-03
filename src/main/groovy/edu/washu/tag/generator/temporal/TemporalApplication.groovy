package edu.washu.tag.generator.temporal

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TemporalApplication {

    static final String TASK_QUEUE = 'data-generator'

    static void main(String[] args) {
        System.setProperty('java.awt.headless', 'true')
        SpringApplication.run(TemporalApplication, args)
    }

}