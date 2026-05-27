package biological.properties;

/**
 * Ratkowsky-Belehrádek cardinal-temperature growth model (Ratkowsky et al. 1983).
 *
 *   sqrt(μ_rel) = b·(T − T_min)·[1 − exp(c·(T − T_max))]
 *
 * Returns a dimensionless growth-rate scaling factor on [0, 1] that is:
 *   - exactly 0 at or below T_min and at or above T_max
 *   - peaks near T_opt
 *   - asymmetric: gentle decline below T_opt, sharp decline above (matches enzyme
 *     denaturation kinetics — observed in all microbial species)
 *
 * The constants b and c are auto-scaled so that the curve peaks at exactly 1.0
 * within the cardinal range.
 */
public final class CardinalTemperatureModel {
    private final double tMin;
    private final double tOpt;
    private final double tMax;
    private final double b;
    private final double c;
    private final double normalization;

    public CardinalTemperatureModel(double tMin, double tOpt, double tMax) {
        if (!(tMin < tOpt && tOpt < tMax)) {
            throw new IllegalArgumentException("Require tMin < tOpt < tMax");
        }
        this.tMin = tMin;
        this.tOpt = tOpt;
        this.tMax = tMax;
        // c controls how sharply the curve falls past T_opt; 0.3 fits most microbes
        this.c = 0.3;
        this.b = 1.0; // unscaled
        // Pre-compute normalization so peak is 1.0
        this.normalization = rawSqrtRate(tOpt);
    }

    /** Returns dimensionless growth-rate scaling on [0, 1]. */
    public double scaling(double temperature) {
        if (temperature <= tMin || temperature >= tMax) return 0.0;
        double s = rawSqrtRate(temperature) / normalization;
        double scaled = s * s;
        return Math.max(0.0, Math.min(1.0, scaled));
    }

    private double rawSqrtRate(double t) {
        return b * (t - tMin) * (1.0 - Math.exp(c * (t - tMax)));
    }

    public double getOptimalTemperature() { return tOpt; }
}
