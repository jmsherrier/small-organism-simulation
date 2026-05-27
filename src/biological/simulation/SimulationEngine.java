package biological.simulation;

import biological.cells.Cell;

/**
 * Runs a discrete time-stepping simulation for a single cell type.
 *
 * At each step the effective growth rate is computed as:
 *   μ_eff = μ_base × f_env(T, pH, S, O₂, I)
 * where f_env is the dimensionless environmental scaling factor [0,1]
 * returned by the cell's Physiology implementation.
 *
 * Population evolves as:  N(t+Δt) = N(t) × exp(μ_eff × Δt)
 */
public class SimulationEngine {
    private static final double TIME_STEP_HOURS = 0.25; // 15-minute integration step

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

            double effectiveGrowth = cell.getGrowthRate() * envEffect;
            population *= Math.exp(effectiveGrowth * TIME_STEP_HOURS);

            result.record(t, population, effectiveGrowth, light);
        }

        return result;
    }
}
