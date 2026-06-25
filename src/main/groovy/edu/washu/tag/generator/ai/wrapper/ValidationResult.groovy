package edu.washu.tag.generator.ai.wrapper

class ValidationResult {

    public static final ValidationResult PASSED = new ValidationResult(passed: true)

    static ValidationResult failBecause(String reason) {
        new ValidationResult(
            passed: false,
            failureCause: reason
        )
    }

    boolean passed
    String failureCause

}
