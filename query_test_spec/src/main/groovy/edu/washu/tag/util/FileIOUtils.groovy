package edu.washu.tag.util

import org.apache.commons.io.IOUtils

import java.nio.charset.StandardCharsets

class FileIOUtils {

    static String readResource(String resourceName) {
        try {
            IOUtils.resourceToString("/${resourceName}", StandardCharsets.UTF_8)
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
    }

}
