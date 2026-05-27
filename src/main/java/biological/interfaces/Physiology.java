package biological.interfaces;

import java.util.Map;

public interface Physiology {
    double getOptimalTemperature();
    double getOptimalpH();
    double getOptimalSalinity();
    double getMaxGrowthRate();
    Map<String, Double> getNutrientRequirements();
    Map<String, Double> getWasteProductionRates();
    boolean canUtilizeNutrient(String nutrient);
    boolean canTolerateStress(String stressType);
    double calculateEnvironmentalEffect(double temperature, double pH, double salinity, double oxygen, double light);
    String getPrimaryEnergySource();
    double getEnergyProductionRate();
    double getStressTolerance(String stressor);
    boolean canFormSpores();

    /**
     * Returns half-saturation constants (Ks) for Monod nutrient kinetics.
     * Keys must match the nutrient names used in SimulationEnvironment.
     * Default implementation returns an empty map (no Monod limitation applied).
     */
    default Map<String, Double> getMonodConstants() {
        return Map.of();
    }

    /**
     * Maintenance energy coefficient m_S (h⁻¹) — fraction of biomass that must be
     * "renewed" per hour to maintain cellular function, independent of growth.
     * Pirt 1965: μ_eff = μ_gross − m_S.
     * Typical values: 0.02–0.05 h⁻¹ for bacteria, 0.01–0.03 h⁻¹ for slow-growing cyanobacteria.
     * Default 0 keeps old behavior for any implementation that hasn't been calibrated.
     */
    default double getMaintenanceCoefficient() {
        return 0.0;
    }
}
