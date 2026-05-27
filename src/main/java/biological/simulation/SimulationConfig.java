package biological.simulation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads simulation parameters from a properties file and provides typed getters with defaults.
 * Supports --config &lt;path&gt; CLI argument to override the default simulation.properties location.
 *
 * <pre>
 * Usage:
 *   java -jar app.jar                          # uses ./simulation.properties if present
 *   java -jar app.jar --config my.properties   # uses the specified file
 * </pre>
 */
public final class SimulationConfig {

    private final Properties props;

    private SimulationConfig(Properties props) {
        this.props = props;
    }

    // ── Factory ───────────────────────────────────────────────────────────

    /** Loads config from CLI args, falling back to simulation.properties, then built-in defaults. */
    public static SimulationConfig fromArgs(String[] args) {
        Optional<Path> configPath = parseConfigArg(args);
        Properties props = new Properties();

        Path resolved = configPath.orElse(Path.of("simulation.properties"));
        if (Files.exists(resolved)) {
            try (InputStream in = Files.newInputStream(resolved)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("Warning: could not read " + resolved + " — using defaults");
            }
        }

        return new SimulationConfig(props);
    }

    private static Optional<Path> parseConfigArg(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("--config".equals(args[i])) {
                return Optional.of(Path.of(args[i + 1]));
            }
        }
        return Optional.empty();
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public double getMed4Duration() {
        return getDouble("sim.med4.duration.hours", 24.0);
    }

    public double getEcoliDuration() {
        return getDouble("sim.ecoli.duration.hours", 12.0);
    }

    public double getYeastDuration() {
        return getDouble("sim.yeast.duration.hours", 24.0);
    }

    public String getDataDir() {
        return props.getProperty("sim.data.dir", "genbank_data");
    }

    public String getOutputDir() {
        return props.getProperty("sim.output.dir", "output");
    }

    /** Returns the verbosity level: "quiet", "normal", or "verbose". */
    public String getVerbosity() {
        return props.getProperty("sim.output.verbosity", "normal");
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private double getDouble(String key, double defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            double parsed = Double.parseDouble(val.trim());
            if (parsed <= 0) throw new IllegalArgumentException(key + " must be positive");
            return parsed;
        } catch (NumberFormatException e) {
            System.err.printf("Warning: invalid value for %s ('%s') — using default %.1f%n",
                key, val, defaultValue);
            return defaultValue;
        }
    }
}
