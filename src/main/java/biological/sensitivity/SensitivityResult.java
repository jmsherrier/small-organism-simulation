package biological.sensitivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Results of sensitivity analysis
 */
public class SensitivityResult {
    private final Map<String, Double> sensitivities;
    private final List<String> mostSensitiveParameters;
    
    public SensitivityResult() {
        this.sensitivities = new HashMap<>();
        this.mostSensitiveParameters = new ArrayList<>();
    }
    
    public void addSensitivity(String parameter, double sensitivity) {
        sensitivities.put(parameter, sensitivity);
        updateMostSensitive(parameter, sensitivity);
    }
    
    private void updateMostSensitive(String parameter, double sensitivity) {
        mostSensitiveParameters.add(parameter);
        mostSensitiveParameters.sort((p1, p2) -> 
            Double.compare(sensitivities.get(p2), sensitivities.get(p1)));
        
        if (mostSensitiveParameters.size() > 5) {
            mostSensitiveParameters.subList(5, mostSensitiveParameters.size()).clear();
        }
    }
    
    public List<String> getMostSensitiveParameters() { return mostSensitiveParameters; }
    public Map<String, Double> getAllSensitivities() { return sensitivities; }
    
    public void printResults() {
        System.out.println("=== SENSITIVITY ANALYSIS ===");
        System.out.println("Most sensitive parameters:");
        for (String param : mostSensitiveParameters) {
            System.out.printf("  %s: %.3f%n", param, sensitivities.get(param));
        }
    }
}
