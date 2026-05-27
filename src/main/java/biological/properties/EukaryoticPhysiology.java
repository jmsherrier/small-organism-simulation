package biological.properties;

import biological.interfaces.Physiology;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Physiology implementation for eukaryotic cells (Saccharomyces cerevisiae).
 * Optimal growth parameters calibrated to yeast literature values.
 */
public class EukaryoticPhysiology implements Physiology {
    private final Map<String, Double> nutrientQuotas;
    private final Map<String, Double> energyYields;
    private double maxGrowthRate;

    public EukaryoticPhysiology() {
        this.maxGrowthRate = 0.5;

        nutrientQuotas = new HashMap<>();
        nutrientQuotas.put("glucose",    0.15);
        nutrientQuotas.put("oxygen",     0.02);
        nutrientQuotas.put("nitrogen",   0.10);
        nutrientQuotas.put("phosphorus", 0.02);
        nutrientQuotas.put("carbon",     0.45);

        energyYields = new HashMap<>();
        energyYields.put("glucose_glycolysis",   2.0);
        energyYields.put("glucose_oxidative",   36.0);
        energyYields.put("fatty_acid_oxidation",120.0);
    }

    public void setMaxGrowthRate(double maxGrowthRate) {
        this.maxGrowthRate = maxGrowthRate;
    }

    @Override public double getMaxGrowthRate()     { return maxGrowthRate; }
    @Override public double getOptimalTemperature() { return 30.0; }
    @Override public double getOptimalpH()          { return 5.5; }
    @Override public double getOptimalSalinity()    { return 0.1; }

    @Override
    public Map<String, Double> getNutrientRequirements() {
        return new HashMap<>(nutrientQuotas);
    }

    @Override
    public Map<String, Double> getWasteProductionRates() {
        Map<String, Double> waste = new HashMap<>();
        waste.put("CO2",     0.15);
        waste.put("ethanol", 0.05);
        waste.put("urea",    0.03);
        return waste;
    }

    @Override
    public boolean canUtilizeNutrient(String nutrient) {
        return nutrientQuotas.containsKey(nutrient);
    }

    @Override
    public boolean canTolerateStress(String stressType) {
        return stressType.equals("osmotic") || stressType.equals("oxidative");
    }

    @Override
    public double calculateEnvironmentalEffect(double temperature, double pH,
                                               double salinity, double oxygen, double light) {
        double tempEffect     = Math.max(0, 1.0 - Math.abs(temperature - 30.0) / 20.0);
        double pHEffect       = Math.max(0, 1.0 - Math.abs(pH - 5.5) / 3.0);
        double salinityEffect = Math.max(0, 1.0 - Math.abs(salinity - 0.1) / 0.15);
        double oxygenEffect   = oxygen / (oxygen + 0.01);
        return Math.min(1, tempEffect * pHEffect * salinityEffect * oxygenEffect);
    }

    @Override public String getPrimaryEnergySource()         { return "glucose"; }
    @Override public double getEnergyProductionRate()        { return 80.0; }
    @Override public boolean canFormSpores()                 { return false; }

    @Override
    public double getStressTolerance(String stressor) {
        return switch (stressor) {
            case "heat"      -> 0.6;
            case "acid"      -> 0.7;
            case "osmotic"   -> 0.7;
            case "oxidative" -> 0.5;
            default          -> 0.5;
        };
    }

    public double getEnergyYield(String substrate, String pathway) {
        return energyYields.getOrDefault(substrate + "_" + pathway, 0.0);
    }

    public Map<String, Double> getEnergyYields() {
        return new HashMap<>(energyYields);
    }

    @Override
    public Map<String, Double> getMonodConstants() {
        // Half-saturation constants for S. cerevisiae batch fermentation
        Map<String, Double> ks = new HashMap<>();
        ks.put("glucose",  1.0);  // Ks ≈ 0.5-2 mg/L for S. cerevisiae (Postma et al. 1989)
        ks.put("nitrogen", 0.5);  // Ks NH₄⁺ ≈ 0.2-1 mM for yeast (Magasanik & Kaiser 2002)
        return Collections.unmodifiableMap(ks);
    }
}
