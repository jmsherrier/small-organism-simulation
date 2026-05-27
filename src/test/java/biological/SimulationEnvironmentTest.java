package biological;

import biological.simulation.SimulationEnvironment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationEnvironmentTest {

    private static final double PEAK = 250.0;

    private SimulationEnvironment marine() {
        return SimulationEnvironment.marinePhotic().build();
    }

    @Test
    void lightIntensity_atDawn_isZero() {
        // sin(0) = 0, so intensity at t=0 should be 0
        assertEquals(0.0, marine().getLightIntensity(0.0), 1e-9);
    }

    @Test
    void lightIntensity_atSolarNoon_isPeak() {
        // 12 h day: midpoint = 6 h → phase=0.25, lightFraction=0.5
        // sin(π × 0.25 / 0.5) = sin(π/2) = 1.0
        assertEquals(PEAK, marine().getLightIntensity(6.0), 1e-6);
    }

    @Test
    void lightIntensity_afterSunset_isZero() {
        // t=12 h → phase=0.5=lightFraction → dark period begins
        assertEquals(0.0, marine().getLightIntensity(12.0), 1e-9);
    }

    @Test
    void lightIntensity_midNight_isZero() {
        assertEquals(0.0, marine().getLightIntensity(18.0), 1e-9);
    }

    @Test
    void lightIntensity_neverExceedsPeak() {
        SimulationEnvironment env = marine();
        for (double t = 0; t < 24; t += 0.5) {
            assertTrue(env.getLightIntensity(t) <= PEAK + 1e-9,
                "Light at t=" + t + " exceeded peak");
        }
    }

    @Test
    void depleteNutrients_reducesConcentration() {
        SimulationEnvironment env = SimulationEnvironment.marinePhotic().build();
        double initialN = env.getNutrientConcentration("nitrogen");
        env.depleteNutrients(java.util.Map.of("nitrogen", 1.0), 1.0);
        assertTrue(env.getNutrientConcentration("nitrogen") < initialN);
    }

    @Test
    void depleteNutrients_doesNotGoBelowZero() {
        SimulationEnvironment env = SimulationEnvironment.marinePhotic().build();
        // Deplete far more than available
        env.depleteNutrients(java.util.Map.of("nitrogen", 1000.0), 1.0);
        assertEquals(0.0, env.getNutrientConcentration("nitrogen"), 1e-9);
    }
}
