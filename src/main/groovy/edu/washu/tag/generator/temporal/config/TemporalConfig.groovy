package edu.washu.tag.generator.temporal.config

import io.temporal.spring.boot.TemporalOptionsCustomizer
import io.temporal.worker.WorkerFactoryOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TemporalConfig {

    @Bean
    TemporalOptionsCustomizer<WorkerFactoryOptions.Builder> customWorkerFactoryOptions() {
        new TemporalOptionsCustomizer<WorkerFactoryOptions.Builder>() {
            @Override
            WorkerFactoryOptions.Builder customize(WorkerFactoryOptions.Builder optionsBuilder) {
                optionsBuilder.setWorkflowCacheSize(1)
                return optionsBuilder
            }
        }
    }

}