package biological;

import biological.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {

    @Test
    void singleMetricWithinTolerance_isValid() {
        ValidationResult result = new ValidationResult();
        result.addMetric("growth_rate", 1.9, 2.0, 0.05, 0.3);
        assertTrue(result.isValid());
    }

    @Test
    void singleMetricExceedingTolerance_isInvalid() {
        ValidationResult result = new ValidationResult();
        result.addMetric("growth_rate", 3.0, 2.0, 0.50, 0.3);
        assertFalse(result.isValid());
    }

    @Test
    void allMetricsWithinTolerance_isValid() {
        ValidationResult result = new ValidationResult();
        result.addMetric("growth_rate", 1.9, 2.0, 0.05, 0.3);
        result.addMetric("wet_mass", 9.8e11, 1.0e12, 0.02, 0.1);
        assertTrue(result.isValid());
    }

    @Test
    void oneFailingMetricMakesOverallInvalid() {
        ValidationResult result = new ValidationResult();
        result.addMetric("growth_rate", 1.9, 2.0, 0.05, 0.3);   // pass
        result.addMetric("wet_mass", 5.0e11, 1.0e12, 0.50, 0.1); // fail
        assertFalse(result.isValid());
    }

    @Test
    void metricsMap_containsAddedMetrics() {
        ValidationResult result = new ValidationResult();
        result.addMetric("growth_rate", 1.9, 2.0, 0.05, 0.3);
        assertTrue(result.getMetrics().containsKey("growth_rate"));
        assertEquals(1.9, result.getMetrics().get("growth_rate").simulated(), 1e-9);
    }
}
