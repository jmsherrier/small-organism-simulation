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
        // μ_max already empirically encompasses mitochondrial/organelle energy overhead
        // (Verduyn et al. 1990 — observed in batch culture, not theoretical maximum).
        // Environmental and Monod factors are applied by SimulationEngine.
        return physiology.getMaxGrowthRate();
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

    public List<Organelle> getOrganelles() { return organelles; }
    public Nucleus getNucleus()            { return nucleus; }

    public double getTotalOrganelleVolume() {
        return organelles.stream().mapToDouble(Organelle::getVolumeMicron3).sum();
    }
}
