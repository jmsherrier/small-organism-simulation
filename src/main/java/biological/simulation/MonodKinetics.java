package biological.simulation;

import java.util.Map;

/**
 * Computes the Liebig-law Monod limitation factor from current nutrient concentrations.
 *
 * The Monod equation for each nutrient is:  f = S / (Ks + S)
 * The overall factor is the minimum across all limiting nutrients (Liebig's law of the minimum).
 * Returns a dimensionless scalar in [0, 1] that scales the effective growth rate.
 */
public class MonodKinetics {

    /**
     * Computes the Monod limitation factor for a cell given current environment nutrient pools.
     *
     * @param halfSaturationConstants  map of nutrient name → Ks (same units as env concentrations)
     * @param env                      the current simulation environment
     * @return                         the minimum Monod factor across all tracked nutrients
     */
    public static double computeLimitationFactor(
            Map<String, Double> halfSaturationConstants,
            SimulationEnvironment env) {

        double minFactor = 1.0;
        for (Map.Entry<String, Double> entry : halfSaturationConstants.entrySet()) {
            String nutrient = entry.getKey();
            double ks = entry.getValue();
            double s = env.getNutrientConcentration(nutrient);
            double factor = s / (ks + s);
            if (factor < minFactor) {
                minFactor = factor;
            }
        }
        return minFactor;
    }
}
