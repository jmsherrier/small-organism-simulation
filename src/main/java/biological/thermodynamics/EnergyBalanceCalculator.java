package biological.thermodynamics;

import java.util.Map;

/**
 * Ensures energy and mass balance in cellular simulations
 */
public class EnergyBalanceCalculator {
    private static final double ATP_HYDROLYSIS_ENERGY = 50.0; // kJ/mol
    private static final double MAINTENANCE_COEFFICIENT = 0.1; // ATP/dry mass/hour
    
    public double calculateEnergyBalance(Map<String, Double> atpProductionRates, 
                                       Map<String, Double> atpConsumptionRates,
                                       double dryMass) {
        double totalProduction = atpProductionRates.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalConsumption = atpConsumptionRates.values().stream().mapToDouble(Double::doubleValue).sum();
        double maintenanceEnergy = calculateMaintenanceEnergy(dryMass);
        
        totalConsumption += maintenanceEnergy;
        
        if (totalConsumption > totalProduction) {
            return totalProduction / totalConsumption; // Energy-limited growth
        } else {
            return 1.0; // Energy-sufficient
        }
    }
    
    public double calculateMaintenanceEnergy(double dryMass) {
        return MAINTENANCE_COEFFICIENT * dryMass * ATP_HYDROLYSIS_ENERGY;
    }
    
    public void validateMassBalance(Map<String, Double> inputFluxes, 
                                  Map<String, Double> outputFluxes,
                                  Map<String, Double> accumulationRates) {
        double tolerance = 1e-6;
        
        for (String metabolite : inputFluxes.keySet()) {
            double input = inputFluxes.getOrDefault(metabolite, 0.0);
            double output = outputFluxes.getOrDefault(metabolite, 0.0);
            double accumulation = accumulationRates.getOrDefault(metabolite, 0.0);
            
            double imbalance = input - output - accumulation;
            
            if (Math.abs(imbalance) > tolerance) {
                throw new ThermodynamicException(
                    "Mass balance violation for " + metabolite + 
                    ": imbalance = " + imbalance);
            }
        }
    }
}
