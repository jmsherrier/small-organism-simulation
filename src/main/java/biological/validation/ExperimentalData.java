package biological.validation;

/**
 * Container for experimental validation data
 */
public record ExperimentalData(
    double growthRate,    // doublings/hour
    double proteinFraction
) {}
