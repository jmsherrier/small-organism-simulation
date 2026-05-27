package biological.simulation;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the abiotic environment for a simulation run.
 * Supports sinusoidal diurnal light cycles and per-step nutrient depletion.
 */
public class SimulationEnvironment {
    private final double temperature;
    private final double pH;
    private final double salinity;
    private final double oxygenConcentration;
    private final double peakLightIntensity;
    private final int dayLengthHours;
    private final Map<String, Double> nutrientConcentrations;

    private SimulationEnvironment(Builder b) {
        this.temperature = b.temperature;
        this.pH = b.pH;
        this.salinity = b.salinity;
        this.oxygenConcentration = b.oxygenConcentration;
        this.peakLightIntensity = b.peakLightIntensity;
        this.dayLengthHours = b.dayLengthHours;
        this.nutrientConcentrations = new HashMap<>(b.nutrients);
    }

    /**
     * Returns irradiance at time t using a half-sine diurnal cycle.
     * Returns 0 during the dark period.
     */
    public double getLightIntensity(double timeHours) {
        double phase = (timeHours % 24.0) / 24.0;
        double lightFraction = (double) dayLengthHours / 24.0;
        if (phase < lightFraction) {
            return peakLightIntensity * Math.sin(Math.PI * phase / lightFraction);
        }
        return 0.0;
    }

    /** Deplete nutrient pools proportional to cellular uptake over a time step. */
    public void depleteNutrients(Map<String, Double> uptakeRates, double timestepHours) {
        for (Map.Entry<String, Double> entry : uptakeRates.entrySet()) {
            double current = nutrientConcentrations.getOrDefault(entry.getKey(), 0.0);
            nutrientConcentrations.put(entry.getKey(),
                Math.max(0.0, current - entry.getValue() * timestepHours));
        }
    }

    public double getNutrientConcentration(String nutrient) {
        return nutrientConcentrations.getOrDefault(nutrient, 1.0);
    }

    public double getTemperature()        { return temperature; }
    public double getPH()                 { return pH; }
    public double getSalinity()           { return salinity; }
    public double getOxygenConcentration(){ return oxygenConcentration; }
    public double getPeakLightIntensity() { return peakLightIntensity; }

    // ── Preset environments ───────────────────────────────────────────────

    /** Open-ocean euphotic zone: 24 °C, pH 8.1, S=0.035, 12 h:12 h L:D. */
    public static Builder marinePhotic() {
        return new Builder()
            .temperature(24.0).pH(8.1).salinity(0.035)
            .oxygen(0.21).peakLight(250.0).dayLength(12)
            .nutrient("nitrogen",   0.5)   // µM — oligotrophic open ocean (Cavender-Bares et al. 2001)
            .nutrient("phosphorus", 0.05)  // µM — North Atlantic HNLC surface values
            .nutrient("carbon",   100.0)
            .nutrient("iron",       0.001); // nM — typical open-ocean dissolved Fe
    }

    /** Standard E. coli laboratory culture: 37 °C, pH 7.0, LB-like nutrients. */
    public static Builder laboratoryAerobic() {
        return new Builder()
            .temperature(37.0).pH(7.0).salinity(0.15)
            .oxygen(0.21).peakLight(0.0).dayLength(0)
            .nutrient("carbon",     200.0)
            .nutrient("nitrogen",    50.0)
            .nutrient("phosphorus",  10.0)
            .nutrient("oxygen",     100.0);
    }

    /** Yeast batch fermentation: 30 °C, pH 5.5, excess glucose. */
    public static Builder yeastFermentation() {
        return new Builder()
            .temperature(30.0).pH(5.5).salinity(0.1)
            .oxygen(0.21).peakLight(0.0).dayLength(0)
            .nutrient("glucose",    100.0)
            .nutrient("nitrogen",    40.0)
            .nutrient("phosphorus",   8.0)
            .nutrient("oxygen",      50.0)
            .nutrient("carbon",     200.0);
    }

    // ── Builder ───────────────────────────────────────────────────────────

    public static class Builder {
        double temperature = 25.0;
        double pH = 7.0;
        double salinity = 0.1;
        double oxygenConcentration = 0.21;
        double peakLightIntensity = 0.0;
        int dayLengthHours = 12;
        final Map<String, Double> nutrients = new HashMap<>();

        public Builder temperature(double v) { temperature = v; return this; }
        public Builder pH(double v)          { pH = v; return this; }
        public Builder salinity(double v)    { salinity = v; return this; }
        public Builder oxygen(double v)      { oxygenConcentration = v; return this; }
        public Builder peakLight(double v)   { peakLightIntensity = v; return this; }
        public Builder dayLength(int v)      { dayLengthHours = v; return this; }
        public Builder nutrient(String k, double v) { nutrients.put(k, v); return this; }
        public SimulationEnvironment build() { return new SimulationEnvironment(this); }
    }
}
