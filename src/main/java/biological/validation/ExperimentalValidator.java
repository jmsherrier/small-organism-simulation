package biological.validation;

import biological.cells.Cell;
import biological.cells.EukaryoticCell;
import biological.organelles.Organelle;
import biological.simulation.SimulationResult;
import java.util.HashMap;
import java.util.Map;

/**
 * Compares simulation output against published experimental measurements.
 *
 * <h3>What this class deliberately does NOT do</h3>
 * It does not "validate" a parameter by reading it back out of the cell and comparing
 * to the literature value it was constructed from — that is a tautology (you would
 * always get 0% error). Such consistency checks live in unit tests instead.
 *
 * <h3>What it does</h3>
 * It compares quantities the simulation actually <em>predicts</em> — peak effective
 * growth rate, net doublings, dry mass from a multi-component sum, and genome mass
 * from parsed CDS lengths — against <em>independent</em> published measurements that
 * were not used to set the simulation's input parameters.
 */
public class ExperimentalValidator {

    public ValidationResult validateCell(Cell cell, String strain) {
        return validateCell(cell, strain, null);
    }

    /**
     * Validate static compositional predictions and (if provided) the simulation's
     * dynamic time-series prediction.
     */
    public ValidationResult validateCell(Cell cell, String strain, SimulationResult sim) {
        ValidationResult result = new ValidationResult();
        validateCompositionalPredictions(cell, strain, result);
        if (sim != null) {
            validateDynamicPrediction(sim, strain, result);
        }
        return result;
    }

    // ── Dynamic prediction: peak effective μ vs independent published measurements ──

    /**
     * Published peak effective growth rates measured at conditions matching the
     * simulation's preset environments. These values are independent of the μ_max
     * constants stored in each Physiology — they come from time-series experiments
     * by different research groups using different methodologies.
     */
    private static final Map<String, Double> PUBLISHED_PEAK_MU_H = Map.of(
        // MED4 in marine photic, diurnal cycle — midday instantaneous rate.
        // Vaulot et al. 1995 (Science): subtropical N. Pacific surface, midday peak μ ≈ 0.04-0.06 h⁻¹
        "MED4",    0.055,
        // E. coli K-12 in LB at 37 °C — exponential phase.
        // Sezonov, Joseleau-Petit & D'Ari 2007 (J. Bacteriol.): doubling time 21 ± 2 min → μ = 1.98 h⁻¹
        "E. coli", 1.98,
        // S. cerevisiae in batch glucose minimal medium at 30 °C.
        // Verduyn, Postma, Scheffers & van Dijken 1990 (J. Gen. Microbiol.): μ_max = 0.45 h⁻¹
        "Yeast",   0.45
    );

    private void validateDynamicPrediction(SimulationResult sim, String strain, ValidationResult result) {
        Double published = PUBLISHED_PEAK_MU_H.get(strain);
        if (published == null) return;
        double simulated = sim.getPeakEffectiveGrowthRate();
        double error = Math.abs(simulated - published) / published;
        // 25% tolerance — accommodates inter-study experimental variability and
        // the model's coarse-grained nature (no flux balance, no detailed kinetics)
        result.addMetric("peak_effective_growth_h", simulated, published, error, 0.25);
    }

    // ── Compositional predictions: simulation's multi-component model vs simpler estimates ──

    private void validateCompositionalPredictions(Cell cell, String strain, ValidationResult result) {
        // Dry mass: simulation sums cytoplasm × dry_fraction + membrane + genome.
        // Expected is wet × empirical dry-mass-fraction (a coarser estimate).
        // Disagreement reflects how well the part-by-part model recovers bulk biomass.
        double simDryMass = cell.getDryDaltonsWithGenome();
        double expDryMass = calculateExpectedDryMass(cell, strain);
        result.addMetric("dry_mass", simDryMass, expDryMass,
            Math.abs(simDryMass - expDryMass) / expDryMass, 0.2);

        // Genome mass: simulation uses GenBank-parsed CDS lengths (variable per gene).
        // Expected uses a published average gene length per organism. Disagreement
        // reflects how representative the avg-length estimate is for the actual genome.
        double simGenomeMass = cell.getGenomeMass();
        double expGenomeMass = calculateExpectedGenomeMass(cell, strain);
        result.addMetric("genome_mass", simGenomeMass, expGenomeMass,
            Math.abs(simGenomeMass - expGenomeMass) / expGenomeMass, 0.15);
    }

    private double calculateExpectedDryMass(Cell cell, String strain) {
        double wetMass = cell.getWetDaltons();
        double dryFraction = switch (strain) {
            case "MED4"    -> 0.33; // Prochlorococcus — Bertilsson et al. 2003 (high pigment)
            case "E. coli" -> 0.30; // E. coli K-12 — Bremer & Dennis 1996
            case "Yeast"   -> 0.28; // S. cerevisiae — Albers et al. 1996
            default        -> 0.28;
        };
        return wetMass * dryFraction;
    }

    private double calculateExpectedGenomeMass(Cell cell, String strain) {
        int geneCount = cell.getCytoplasm().getNucleoid().getGenes().size();
        // Average CDS length from NCBI RefSeq per organism
        double avgGeneLength = switch (strain) {
            case "Yeast"   -> 1400.0;
            case "E. coli" -> 1200.0;
            default        -> 1000.0;
        };
        return geneCount * avgGeneLength * 650.0; // 650 Da/bp dsDNA average mass
    }

    // ── Auxiliary: total protein mass from cellular composition ──

    public double calculateTotalProteinMass(Cell cell) {
        int count = cell.getCytoplasm().getSolubleProteins().size();
        count += cell.getCytoplasm().getMembrane().getMembraneProteins().size();
        if (cell instanceof EukaryoticCell euk) {
            for (Organelle organelle : euk.getOrganelles()) {
                count += organelle.getProteins().size();
            }
        }
        return count * 40_000.0; // ~40 kDa average protein mass in Da
    }

    public double getProteinFraction(String strain) {
        return switch (strain) {
            case "MED4"    -> 0.55;
            case "E. coli" -> 0.50;
            case "Yeast"   -> 0.45;
            default        -> 0.50;
        };
    }
}
