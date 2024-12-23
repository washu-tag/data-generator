package edu.washu.tag.generator.util

import java.nio.charset.StandardCharsets
import org.apache.commons.io.IOUtils

class FileIOUtils {

    static String readResource(String resourceName) {
        try {
            IOUtils.resourceToString("/${resourceName}", StandardCharsets.UTF_8)
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

}
