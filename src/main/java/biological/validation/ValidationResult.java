package biological.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Results of model validation against experimental data
 */
public class ValidationResult {
    private final Map<String, ValidationMetric> metrics;
    private boolean overallValid;
    
    public ValidationResult() {
        this.metrics = new HashMap<>();
        this.overallValid = true;
    }
    
    public void addMetric(String name, double simulated, double expected, 
                         double error, double tolerance) {
        ValidationMetric metric = new ValidationMetric(name, simulated, expected, error, tolerance);
        metrics.put(name, metric);
        
        if (error > tolerance) {
            overallValid = false;
        }
    }
    
    public boolean isValid() { return overallValid; }
    public Map<String, ValidationMetric> getMetrics() { return metrics; }
    
    public void printResults() {
        System.out.println("=== VALIDATION RESULTS ===");
        for (ValidationMetric metric : metrics.values()) {
            System.out.printf("%s: simulated=%.3f, expected=%.3f, error=%.1f%% %s%n",
                metric.name(), metric.simulated(), metric.expected(),
                metric.error() * 100, metric.isWithinTolerance() ? "✓" : "✗");
        }
        System.out.println("Overall validation: " + (isValid() ? "PASS" : "FAIL"));
    }
}
