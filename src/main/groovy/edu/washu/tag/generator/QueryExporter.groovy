package edu.washu.tag.generator

import edu.washu.tag.generator.query.QueryGenerator

class QueryExporter {

    static void main(String[] args) {
        final YamlObjectMapper yamlObjectMapper = new YamlObjectMapper()
        final QueryGenerator queryGenerator = new QueryGenerator()
        (args[0].split(',') as List<String>).each { batchPath ->
            final BatchSpecification batchSpecification = yamlObjectMapper.readValue(new File(batchPath), BatchSpecification)
            queryGenerator.processData(batchSpecification)
        }

        queryGenerator.writeQueries()
    }

}
