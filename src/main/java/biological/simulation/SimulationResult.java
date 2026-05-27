package biological.simulation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores time-series output from a SimulationEngine run and provides summary statistics.
 * Each recorded time point holds: [time (h), population (relative), growth rate (h⁻¹), irradiance].
 */
public class SimulationResult {
    private final String cellName;
    private final List<double[]> timePoints;
    private double maxGrowthRate = 0.0;

    public SimulationResult(String cellName) {
        this.cellName = cellName;
        this.timePoints = new ArrayList<>();
    }

    public void record(double time, double population, double growthRate, double light) {
        timePoints.add(new double[]{time, population, growthRate, light});
        if (growthRate > maxGrowthRate) maxGrowthRate = growthRate;
    }

    // ── Summary statistics ────────────────────────────────────────────────

    public double getFinalPopulation() {
        return timePoints.isEmpty() ? 1.0 : timePoints.get(timePoints.size() - 1)[1];
    }

    /** Minimum doubling time (h), derived from peak growth rate. */
    public double getMinDoublingTime() {
        return maxGrowthRate > 0 ? Math.log(2) / maxGrowthRate : Double.POSITIVE_INFINITY;
    }

    /** Total doublings over the simulation run. */
    public double getNetDoublings() {
        double finalPop = getFinalPopulation();
        return finalPop > 1.0 ? Math.log(finalPop) / Math.log(2) : 0.0;
    }

    public double getMaxGrowthRate() { return maxGrowthRate; }

    /**
     * Peak effective specific growth rate (h⁻¹) observed during the run.
     * Unlike μ_max stored in physiology, this is what the simulation actually predicts:
     * it emerges from μ_max combined with environmental scaling, Monod limitation,
     * and maintenance subtraction at the conditions of the run.
     */
    public double getPeakEffectiveGrowthRate() { return maxGrowthRate; }
    public String getCellName()      { return cellName; }
    public List<double[]> getTimePoints() { return timePoints; }

    // ── Display ───────────────────────────────────────────────────────────

    public void printTimeSeries(int maxRows) {
        int total = timePoints.size();
        int step = Math.max(1, total / maxRows);
        System.out.printf("  %-8s %-14s %-14s %-12s%n",
            "Hour", "Light(µE/m²s)", "Growth(h⁻¹)", "Population");
        System.out.println("  " + "─".repeat(52));
        for (int i = 0; i < total; i += step) {
            double[] p = timePoints.get(i);
            System.out.printf("  %-8.1f %-14.1f %-14.4f %-12.3f%n",
                p[0], p[3], p[2], p[1]);
        }
    }

    public void printSummary() {
        System.out.printf("  Peak growth rate : %.4f h⁻¹%n", maxGrowthRate);
        System.out.printf("  Min doubling time: %.2f h%n", getMinDoublingTime());
        System.out.printf("  Net doublings    : %.2f (over %.1f h)%n",
            getNetDoublings(), timePoints.isEmpty() ? 0 : timePoints.get(timePoints.size() - 1)[0]);
        System.out.printf("  Final population : %.2fx initial%n", getFinalPopulation());
    }

    // ── Export ────────────────────────────────────────────────────────────

    /**
     * Writes the full time series to a CSV file.
     * Columns: time_h, light_uE, growth_rate_h, population_rel
     */
    public void exportCsv(Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(outputPath)) {
            w.write("time_h,light_uE,growth_rate_h,population_rel\n");
            for (double[] p : timePoints) {
                w.write(String.format("%.4f,%.4f,%.6f,%.6f%n", p[0], p[3], p[2], p[1]));
            }
        }
    }

    /**
     * Writes a JSON summary of key statistics.
     * Hand-rolled — no external dependency.
     */
    public void exportSummaryJson(Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        double duration = timePoints.isEmpty() ? 0 : timePoints.get(timePoints.size() - 1)[0];
        String json = String.format("""
            {
              "cell": "%s",
              "duration_h": %.1f,
              "peak_growth_rate_h": %.6f,
              "min_doubling_time_h": %.4f,
              "net_doublings": %.4f,
              "final_population_fold": %.4f
            }
            """,
            cellName, duration,
            maxGrowthRate, getMinDoublingTime(),
            getNetDoublings(), getFinalPopulation());
        Files.writeString(outputPath, json);
    }
}
