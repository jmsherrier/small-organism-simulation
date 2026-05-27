package biological.properties;

import biological.interfaces.Physiology;
import java.util.*;

/**
 * Physiology implementation for heterotrophic bacteria
 */
public class BacterialPhysiology implements Physiology {
    private final Map<String, Double> nutrientQuotas;
    // E. coli K-12 cardinal temperatures (Ratkowsky et al. 1983; Membré et al. 2005)
    private static final CardinalTemperatureModel TEMP_MODEL =
        new CardinalTemperatureModel(7.5, 37.0, 49.0);

    public BacterialPhysiology() {
        nutrientQuotas = new HashMap<>();
        nutrientQuotas.put("carbon", 0.5);
        nutrientQuotas.put("nitrogen", 0.12);
        nutrientQuotas.put("phosphorus", 0.03);
        nutrientQuotas.put("oxygen", 0.0);
    }
    
    @Override
    public double getOptimalTemperature() { return 37.0; }
    @Override
    public double getOptimalpH() { return 7.0; }
    @Override
    public double getOptimalSalinity() {
        return 0.15;
    }

    private double maxGrowthRate = 1.7; // h⁻¹ — Monod 1949; E. coli K-12 in rich medium
    @Override
    public double getMaxGrowthRate() {
        return maxGrowthRate;
    }
    public void setMaxGrowthRate(double maxGrowthRate) {
        this.maxGrowthRate = maxGrowthRate;
    }
    
    @Override
    public Map<String, Double> getNutrientRequirements() { 
        return Collections.unmodifiableMap(nutrientQuotas); 
    }
    
    @Override
    public Map<String, Double> getWasteProductionRates() {
        Map<String, Double> waste = new HashMap<>();
        waste.put("CO2", 0.2);
        waste.put("ammonia", 0.05);
        return waste;
    }
    
    @Override
    public boolean canUtilizeNutrient(String nutrient) {
        return nutrientQuotas.containsKey(nutrient);
    }
    
    @Override
    public boolean canTolerateStress(String stressType) {
        return stressType.equals("heat") || stressType.equals("acid");
    }
    
    @Override
    public double calculateEnvironmentalEffect(double temperature, double pH, double salinity,
                                             double oxygen, double light) {
        double tempEffect = TEMP_MODEL.scaling(temperature);
        double pHEffect = 1.0 - Math.abs(pH - 7.0) / 3.0;
        double salinityEffect = 1.0 - Math.abs(salinity - 0.15) / 0.1;
        return Math.max(0, Math.min(1, tempEffect * pHEffect * salinityEffect));
    }
    
    @Override public String getPrimaryEnergySource() { return "glucose"; }
    @Override public double getEnergyProductionRate() { return 100.0; }
    @Override public double getStressTolerance(String stressor) { return 0.7; }
    @Override public boolean canFormSpores() { return true; }

    @Override
    public double getMaintenanceCoefficient() {
        // E. coli K-12 maintenance ≈ 0.04 h⁻¹ (Pirt 1965; Tempest & Neijssel 1984)
        return 0.04;
    }

    @Override
    public Map<String, Double> getMonodConstants() {
        // Half-saturation constants for E. coli K-12 in LB-like medium (concentration units match env)
        Map<String, Double> ks = new HashMap<>();
        ks.put("carbon",   1.0);  // Ks glucose ≈ 1 mg/L (Shehata & Marr 1971; Pirt 1975)
        ks.put("nitrogen", 0.5);  // Ks NH₄⁺ ≈ 0.1-1 mg/L (Vallino et al. 1994)
        return Collections.unmodifiableMap(ks);
    }
}
