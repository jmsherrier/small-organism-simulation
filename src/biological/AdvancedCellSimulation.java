package biological;

import biological.cells.*;
import biological.components.Gene;
import biological.factory.CellFactory;
import biological.sensitivity.SensitivityAnalyzer;
import biological.sensitivity.SensitivityResult;
import biological.simulation.SimulationEngine;
import biological.simulation.SimulationEnvironment;
import biological.simulation.SimulationResult;
import biological.util.GenBankParser;
import biological.util.YeastGeneLoader;
import biological.validation.ExperimentalValidator;
import biological.validation.ValidationResult;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Entry point for the Prochlorococcus Cellular Simulation Framework.
 * Runs genomic data ingestion, static property display, time-series simulation,
 * experimental validation, and sensitivity analysis for three cell types.
 */
public class AdvancedCellSimulation {

    private static final String DATA_DIR = "genbank_data";
    private static final String LINE = "═".repeat(60);
    private static final String THIN = "─".repeat(60);

    public static void main(String[] args) {
        printHeader();

        try {
            java.nio.file.Files.createDirectories(Paths.get(DATA_DIR));

            // ── 1. Genomic data ingestion ─────────────────────────────────
            section("1. GENOMIC DATA INGESTION");
            List<Gene> med4Genes  = fetchGenes("BX548174", DATA_DIR + "/MED4.gb",  "Prochlorococcus MED4");
            List<Gene> ecoliGenes = fetchGenes("U00096",   DATA_DIR + "/ECOLI.gb", "Escherichia coli K-12");
            List<Gene> yeastGenes = loadYeastGenes();

            // ── 2. Cellular properties ────────────────────────────────────
            section("2. CELLULAR PROPERTIES");
            Cell med4Cell  = CellFactory.createCell("photosynthetic", "MED4",    med4Genes,  0.6,  0.30);
            Cell ecoliCell = CellFactory.createCell("heterotrophic",  "E. coli", ecoliGenes, 1.0,  0.25);
            Cell yeastCell = CellFactory.createCell("eukaryotic",     "Yeast",   yeastGenes, 10.0, 0.28);

            printCellTable(med4Cell, ecoliCell, yeastCell);

            // ── 3. Time-series simulation ─────────────────────────────────
            section("3. TIME-SERIES SIMULATION  (24 h, Δt = 15 min)");
            SimulationEngine engine = new SimulationEngine();

            runAndPrint(engine, med4Cell,
                SimulationEnvironment.marinePhotic().build(),
                "Prochlorococcus MED4 — marine euphotic zone, 12 h:12 h L:D",
                24.0, 12);

            runAndPrint(engine, ecoliCell,
                SimulationEnvironment.laboratoryAerobic().build(),
                "Escherichia coli K-12 — laboratory aerobic, 37 °C",
                12.0, 8);

            runAndPrint(engine, yeastCell,
                SimulationEnvironment.yeastFermentation().build(),
                "Saccharomyces cerevisiae — batch fermentation, 30 °C",
                24.0, 8);

            // ── 4. Experimental validation ────────────────────────────────
            section("4. EXPERIMENTAL VALIDATION");
            ExperimentalValidator validator = new ExperimentalValidator();
            printValidation(validator, med4Cell,  "MED4");
            printValidation(validator, ecoliCell, "E. coli");
            printValidation(validator, yeastCell, "Yeast");

            // ── 5. Sensitivity analysis ───────────────────────────────────
            section("5. SENSITIVITY ANALYSIS  (MED4, 10% parameter perturbation)");
            SensitivityAnalyzer sensitivityAnalyzer = new SensitivityAnalyzer();
            List<String> params = Arrays.asList("max_growth_rate", "dry_fraction");
            SensitivityResult sensitivity = sensitivityAnalyzer.analyzeCell(med4Cell, params);
            sensitivity.printResults();

            System.out.println();
            System.out.println(LINE);
            System.out.println("  Simulation completed successfully.");
            System.out.println(LINE);

        } catch (IOException e) {
            System.err.println("Genomic data error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Simulation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static void printHeader() {
        System.out.println(LINE);
        System.out.println("  PROCHLOROCOCCUS CELLULAR SIMULATION FRAMEWORK");
        System.out.println("  Version 2.3  |  Java 21  |  NCBI GenBank Integration");
        System.out.println(LINE);
        System.out.println();
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(THIN);
        System.out.println("  " + title);
        System.out.println(THIN);
    }

    private static List<Gene> fetchGenes(String accession, String path, String label) throws IOException {
        GenBankParser.downloadGenBankFile(accession, path);
        List<Gene> genes = GenBankParser.parseGenBankFile(path);
        System.out.printf("  ✓ %-30s [%s]  %,d genes%n", label, accession, genes.size());
        return genes;
    }

    private static List<Gene> loadYeastGenes() {
        List<Gene> genes = YeastGeneLoader.loadYeastGenes();
        System.out.printf("  ✓ %-30s %,d genes  (16 chromosomes, synthetic)%n",
            "Saccharomyces cerevisiae", genes.size());
        return genes;
    }

    private static void printCellTable(Cell... cells) {
        System.out.printf("  %-16s %-12s %-16s %-16s %-16s%n",
            "Cell", "Volume(µm³)", "Wet Mass(Da)", "Dry Mass(Da)", "Genome Mass(Da)");
        System.out.println("  " + "─".repeat(78));
        for (Cell c : cells) {
            System.out.printf("  %-16s %-12.2f %-16.3e %-16.3e %-16.3e%n",
                c.getStrain(),
                c.getVolumeMicron3(),
                c.getWetDaltons(),
                c.getDryDaltonsWithGenome(),
                c.getGenomeMass());
        }
    }

    private static void runAndPrint(SimulationEngine engine, Cell cell,
                                    SimulationEnvironment env,
                                    String label, double hours, int rows) {
        System.out.println();
        System.out.println("  " + label);
        SimulationResult result = engine.runSimulation(cell, env, hours);
        result.printTimeSeries(rows);
        System.out.println();
        result.printSummary();
    }

    private static void printValidation(ExperimentalValidator validator, Cell cell, String strain) {
        System.out.println();
        System.out.println("  [" + strain + "]");
        ValidationResult r = validator.validateCell(cell, strain);
        r.printResults();
    }
}
