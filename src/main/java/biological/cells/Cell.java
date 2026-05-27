package biological.cells;

import biological.components.Cytoplasm;
import biological.components.PlasmaMembrane;
import biological.interfaces.GenomeProperties;
import biological.interfaces.Physiology;
import biological.thermodynamics.EnergyBalanceCalculator;
import java.util.Map;

/**
 * Base class for all cell types with fundamental cellular components
 */
public abstract class Cell {
    protected final String strain;
    protected final double volumeMicron3;
    protected double dryFraction;
    protected final Cytoplasm cytoplasm;
    protected final GenomeProperties genomeProperties;
    protected final Physiology physiology;
    protected final PlasmaMembrane membrane;
    protected final EnergyBalanceCalculator energyCalculator;
    
    public Cell(String strain, double volumeMicron3, double dryFraction,
               Cytoplasm cytoplasm, GenomeProperties genomeProperties,
            Physiology physiology, PlasmaMembrane membrane) {
        this.strain = strain;
        this.volumeMicron3 = volumeMicron3;
        this.dryFraction = dryFraction;
        this.cytoplasm = cytoplasm;
        this.genomeProperties = genomeProperties;
        this.physiology = physiology;
        this.membrane = membrane;
        this.energyCalculator = new EnergyBalanceCalculator();
    }

    public void setDryFraction(double dryFraction) {
        this.dryFraction = dryFraction;
    }
    
    // Common methods for all cells
    public abstract double getGrowthRate();
    public abstract Map<String, Double> getNutrientUptakeRates();
    
    public double getWetDaltons() {
        return biological.util.CellConversion.volumeToWetDaltons(volumeMicron3);
    }
    
    public double getGenomeMass() {
        return biological.util.CellConversion.genomeToDaltons(cytoplasm.getNucleoid().getGenes());
    }
    
    public double getDryDaltonsWithGenome() {
        double cytoplasmDry = biological.util.CellConversion.volumeToDryDaltons(cytoplasm.getVolume(), dryFraction);
        double membraneDry = membrane.getTotalMass();
        return cytoplasmDry + membraneDry + getGenomeMass();
    }
    
    public double calculateEnergyBalance() {
        Map<String, Double> atpProduction = calculateATPProduction();
        Map<String, Double> atpConsumption = calculateATPConsumption();
        double dryMass = getDryDaltonsWithGenome() / 6.022e23 * 1e-3; // Convert to grams
        
        return energyCalculator.calculateEnergyBalance(atpProduction, atpConsumption, dryMass);
    }
    
    protected double calculateNutrientLimitation() {
        // Simplified nutrient limitation - should be overridden by subclasses
        Map<String, Double> uptakeRates = getNutrientUptakeRates();
        Map<String, Double> requirements = physiology.getNutrientRequirements();
        
        double minSufficiency = 1.0;
        for (String nutrient : requirements.keySet()) {
            double required = requirements.get(nutrient);
            double available = uptakeRates.getOrDefault(nutrient, 0.0);
            if (required > 0) {
                double sufficiency = Math.min(available / required, 1.0);
                minSufficiency = Math.min(minSufficiency, sufficiency);
            }
        }
        
        return minSufficiency;
    }
    
    protected abstract Map<String, Double> calculateATPProduction();
    protected abstract Map<String, Double> calculateATPConsumption();
    
    // Getters
    public String getStrain() { return strain; }
    public double getVolumeMicron3() { return volumeMicron3; }
    public double getDryFraction() { return dryFraction; }
    public Cytoplasm getCytoplasm() { return cytoplasm; }
    public GenomeProperties getGenomeProperties() { return genomeProperties; }
    public Physiology getPhysiology() { return physiology; }
    public PlasmaMembrane getMembrane() { return membrane; }
}
