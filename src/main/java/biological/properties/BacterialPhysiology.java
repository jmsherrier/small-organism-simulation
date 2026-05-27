package biological.properties;

import biological.interfaces.Physiology;
import java.util.*;

/**
 * Physiology implementation for heterotrophic bacteria
 */
public class BacterialPhysiology implements Physiology {
    private final Map<String, Double> nutrientQuotas;
    
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

    private double maxGrowthRate = 2.0;
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
        double tempEffect = 1.0 - Math.abs(temperature - 37.0) / 20.0;
        double pHEffect = 1.0 - Math.abs(pH - 7.0) / 3.0;
        double salinityEffect = 1.0 - Math.abs(salinity - 0.15) / 0.1;
        return Math.max(0, Math.min(1, tempEffect * pHEffect * salinityEffect));
    }
    
    @Override public String getPrimaryEnergySource() { return "glucose"; }
    @Override public double getEnergyProductionRate() { return 100.0; }
    @Override public double getStressTolerance(String stressor) { return 0.7; }
    @Override public boolean canFormSpores() { return true; }

    @Override
    public Map<String, Double> getMonodConstants() {
        // Half-saturation constants for E. coli K-12 in LB-like medium (concentration units match env)
        Map<String, Double> ks = new HashMap<>();
        ks.put("carbon",   10.0);  // Monod Ks for glucose ~10 mg/L equivalent
        ks.put("nitrogen",  5.0);  // Ks for ammonium ~5 mg/L equivalent
        return Collections.unmodifiableMap(ks);
    }
}
