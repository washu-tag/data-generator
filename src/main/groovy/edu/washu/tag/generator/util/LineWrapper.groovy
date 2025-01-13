package edu.washu.tag.generator.util

class LineWrapper {

    private static final int LINE_LENGTH_WRAP = 70

    static List<String> splitLongLines(String inputTextBlock, int maxLineLength = LINE_LENGTH_WRAP) {
        inputTextBlock.split('\n').collect { line ->
            splitLongLinesIntermediate([], line, maxLineLength)
        }.flatten() as List<String>
    }

    private static List<String> splitLongLinesIntermediate(List<String> split, String remainingString, int maxLineLength) {
        if (remainingString.length() < maxLineLength) {
            split << remainingString
            return split
        }
        final int lastSpace = remainingString.substring(0, maxLineLength).lastIndexOf(' ')
        split << remainingString.substring(0, lastSpace).trim()
        splitLongLinesIntermediate(split, remainingString.substring(lastSpace + 1), maxLineLength)
    }

}
