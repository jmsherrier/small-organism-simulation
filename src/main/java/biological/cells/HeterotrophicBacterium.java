package biological.cells;

import biological.components.Cytoplasm;
import biological.components.PlasmaMembrane;
import biological.interfaces.GenomeProperties;
import biological.interfaces.Physiology;
import biological.properties.RespirationProperties;
import java.util.*;

/**
 * Heterotrophic bacterial cell (e.g., Escherichia coli).
 * Growth rate is governed by maximum physiology rate scaled by respiration efficiency.
 */
public class HeterotrophicBacterium extends Cell {
    private final RespirationProperties respiration;

    public HeterotrophicBacterium(String strain, double volumeMicron3, double dryFraction,
                                Cytoplasm cytoplasm, GenomeProperties genomeProperties,
                                Physiology physiology, PlasmaMembrane membrane,
                                RespirationProperties respiration) {
        super(strain, volumeMicron3, dryFraction, cytoplasm, genomeProperties, physiology, membrane);
        this.respiration = respiration;
    }

    @Override
    public double getGrowthRate() {
        return physiology.getMaxGrowthRate() * respiration.getRespirationEfficiency();
    }

    @Override
    public Map<String, Double> getNutrientUptakeRates() {
        Map<String, Double> rates = new HashMap<>();
        Map<String, Double> requirements = physiology.getNutrientRequirements();
        for (String nutrient : requirements.keySet()) {
            if (physiology.canUtilizeNutrient(nutrient)) {
                double uptake = requirements.get(nutrient) * membrane.getSurfaceArea() * 0.0001;
                rates.put(nutrient, uptake);
            }
        }
        return rates;
    }

    @Override
    protected Map<String, Double> calculateATPProduction() {
        Map<String, Double> production = new HashMap<>();
        production.put("respiration", getRespirationRate());
        return production;
    }

    @Override
    protected Map<String, Double> calculateATPConsumption() {
        Map<String, Double> consumption = new HashMap<>();
        consumption.put("biosynthesis", getGrowthRate() * 800);
        consumption.put("maintenance",  40.0);
        return consumption;
    }

    public double getRespirationRate()  { return respiration.calculateRespirationRate(); }
    public boolean canRespire()         { return respiration.canPerformAerobicRespiration(); }
}
