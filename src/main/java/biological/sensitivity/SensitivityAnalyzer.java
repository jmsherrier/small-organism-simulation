package biological.sensitivity;

import biological.cells.Cell;
import biological.cells.MED4Strain;
import biological.properties.BacterialPhysiology;
import biological.properties.EukaryoticPhysiology;
import java.util.List;

/**
 * Analyzes sensitivity of model outputs to parameter changes
 */
public class SensitivityAnalyzer {
    
    public SensitivityResult analyzeCell(Cell cell, List<String> parametersToTest) {
        SensitivityResult result = new SensitivityResult();
        
        double baseGrowthRate = cell.getGrowthRate();
        
        for (String param : parametersToTest) {
            double sensitivity = analyzeParameterSensitivity(cell, param, baseGrowthRate);
            result.addSensitivity(param, sensitivity);
        }
        
        return result;
    }
    
    private double analyzeParameterSensitivity(Cell cell, String parameter, double baseOutput) {
        double perturbation = 0.1; // 10% change
        double originalValue = getParameterValue(cell, parameter);
        
        if (originalValue == 0.0) return 0.0; // Skip zero parameters
        
        setParameterValue(cell, parameter, originalValue * (1 + perturbation));
        double positiveOutput = cell.getGrowthRate();
        
        setParameterValue(cell, parameter, originalValue * (1 - perturbation));
        double negativeOutput = cell.getGrowthRate();
        
        setParameterValue(cell, parameter, originalValue); // Restore original
        
        double positiveSensitivity = Math.abs((positiveOutput - baseOutput) / baseOutput) / perturbation;
        double negativeSensitivity = Math.abs((baseOutput - negativeOutput) / baseOutput) / perturbation;
        
        return (positiveSensitivity + negativeSensitivity) / 2;
    }
    
    private double getParameterValue(Cell cell, String parameter) {
        return switch (parameter) {
            case "max_growth_rate" -> cell.getPhysiology().getMaxGrowthRate();
            case "dry_fraction" -> cell.getDryFraction();
            case "volume" -> cell.getVolumeMicron3();
            default -> 0.0;
        };
    }
    
    private void setParameterValue(Cell cell, String parameter, double value) {
        try {
            switch (parameter) {
                case "max_growth_rate" -> {
                    switch (cell.getPhysiology()) {
                        case BacterialPhysiology b -> b.setMaxGrowthRate(value);
                        case EukaryoticPhysiology e -> e.setMaxGrowthRate(value);
                        case MED4Strain.MED4Physiology m -> m.setMaxGrowthRate(value);
                        default -> {}
                    }
                }
                    
                case "dry_fraction" -> cell.setDryFraction(value);
                    
                case "volume" -> {
                }
            }
            // Volume modification would require more complex changes to all components
            // For now, we'll skip this parameter
                    } catch (Exception e) {
            System.err.println("Error setting parameter " + parameter + ": " + e.getMessage());
        }
    }
}
