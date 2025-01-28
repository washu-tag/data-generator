package edu.washu.tag.generator.util

import edu.washu.tag.util.FileIOUtils
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class LineWrapperTest {

    @Test
    void testWrap() {
        assertEquals(
            LineWrapper.splitLongLines(FileIOUtils.readResource('test_text_wrap_1.txt'), 10),
            [
                'abc',
                '12345',
                '67890',
                '123456789',
                '0123',
                '12345678',
                '90123456',
                '123456789',
                '',
                'a'
            ]
        )
    }

}
