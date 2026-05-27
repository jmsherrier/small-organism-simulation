package biological.validation;

/**
 * Individual validation metric with tolerance checking
 */
public record ValidationMetric(
    String name,
    double simulated,
    double expected,
    double error,
    double tolerance
) {
    public boolean isWithinTolerance() { return error <= tolerance; }
}
