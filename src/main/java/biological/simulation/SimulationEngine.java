package biological.simulation;

import biological.cells.Cell;

/**
 * Runs a discrete time-stepping simulation for a single cell type.
 *
 * At each step the effective growth rate is computed as:
 *   μ_eff = μ_base × f_env(T, pH, S, O₂, I) × f_monod(nutrients)
 * where f_env is the dimensionless multi-factor environmental scaling [0,1] and
 * f_monod is the Liebig-law Monod nutrient limitation factor [0,1].
 *
 * Nutrient pools are depleted each step proportional to uptake rates × population.
 * Population evolves as:  N(t+Δt) = N(t) × exp(μ_eff × Δt)
 */
public class SimulationEngine {
    static final double TIME_STEP_HOURS = 0.25; // 15-minute integration step

    /**
     * Runs the simulation for {@code durationHours} hours and returns a full time series.
     *
     * @param cell          the cell type to simulate
     * @param env           environmental conditions (mutated by nutrient depletion)
     * @param durationHours total run duration in hours
     */
    public SimulationResult runSimulation(Cell cell, SimulationEnvironment env, double durationHours) {
        SimulationResult result = new SimulationResult(cell.getStrain());
        double population = 1.0;

        for (double t = 0; t <= durationHours + 1e-9; t += TIME_STEP_HOURS) {
            double light = env.getLightIntensity(t);

            double envEffect = cell.getPhysiology().calculateEnvironmentalEffect(
                env.getTemperature(),
                env.getPH(),
                env.getSalinity(),
                env.getOxygenConcentration(),
                light);

            double monodFactor = MonodKinetics.computeLimitationFactor(
                cell.getPhysiology().getMonodConstants(), env);

            double effectiveGrowth = cell.getGrowthRate() * envEffect * monodFactor;
            population *= Math.exp(effectiveGrowth * TIME_STEP_HOURS);

            // Deplete nutrients stoichiometrically: consumed ∝ growth rate × quota × population
            final double g = effectiveGrowth;
            final double pop = population;
            java.util.Map<String, Double> quotas = cell.getPhysiology().getNutrientRequirements();
            java.util.Map<String, Double> depletion = new java.util.HashMap<>();
            quotas.forEach((k, v) -> depletion.put(k, g * v * pop));
            env.depleteNutrients(depletion, TIME_STEP_HOURS);

            result.record(t, population, effectiveGrowth, light);
        }

        return result;
    }
}
