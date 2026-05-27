package biological.cells;

import biological.components.Cytoplasm;
import biological.components.PlasmaMembrane;
import biological.interfaces.GenomeProperties;
import biological.interfaces.Physiology;
import biological.organelles.Nucleus;
import biological.organelles.Organelle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eukaryotic cell with membrane-bound nucleus, mitochondria, and energy balance.
 */
public class EukaryoticCell extends Cell {
    private final List<Organelle> organelles;
    private final Nucleus nucleus;

    public EukaryoticCell(String strain, double volumeMicron3, double dryFraction,
                        Cytoplasm cytoplasm, GenomeProperties genomeProperties,
                        Physiology physiology, PlasmaMembrane membrane,
                        Nucleus nucleus, List<Organelle> organelles) {
        super(strain, volumeMicron3, dryFraction, cytoplasm, genomeProperties, physiology, membrane);
        this.nucleus = nucleus;
        this.organelles = organelles;
    }

    @Override
    public double getGrowthRate() {
        double baseRate = physiology.getMaxGrowthRate();
        double energyBalance = calculateEnergyBalance();
        double nutrientLimitation = calculateNutrientLimitation();
        double yeastGrowthFactor = calculateYeastGrowthFactor();
        return baseRate * energyBalance * nutrientLimitation * yeastGrowthFactor;
    }

    private double calculateYeastGrowthFactor() {
        // Oxidative phosphorylation coupling efficiency ~0.9 (Brand 2005)
        double mitochondrialEfficiency = 0.9;
        // Organelle maintenance overhead ~5% of total energy (Rolfe & Brown 1997)
        double organelleEfficiency = 0.95;
        return mitochondrialEfficiency * organelleEfficiency;
    }

    @Override
    public Map<String, Double> getNutrientUptakeRates() {
        Map<String, Double> rates = new HashMap<>();
        Map<String, Double> requirements = physiology.getNutrientRequirements();
        double surfaceArea = getMembrane().getSurfaceArea();
        for (String nutrient : requirements.keySet()) {
            double requirement = requirements.get(nutrient);
            rates.put(nutrient, requirement * surfaceArea * 0.005);
        }
        return rates;
    }

    @Override
    protected Map<String, Double> calculateATPProduction() {
        Map<String, Double> production = new HashMap<>();
        production.put("mitochondrial_oxidative_phosphorylation", 200.0);
        production.put("glycolysis", 50.0);
        return production;
    }

    @Override
    protected Map<String, Double> calculateATPConsumption() {
        Map<String, Double> consumption = new HashMap<>();
        consumption.put("biosynthesis",         80.0);
        consumption.put("maintenance",          20.0);
        consumption.put("transport",             5.0);
        consumption.put("organelle_maintenance", 10.0);
        return consumption;
    }

    /** Dry mass uses a fixed 28% fraction matching yeast experimental data. */
    @Override
    public double getDryDaltonsWithGenome() {
        return getWetDaltons() * 0.28;
    }

    public List<Organelle> getOrganelles() { return organelles; }
    public Nucleus getNucleus()            { return nucleus; }

    public double getTotalOrganelleVolume() {
        return organelles.stream().mapToDouble(Organelle::getVolumeMicron3).sum();
    }
}
