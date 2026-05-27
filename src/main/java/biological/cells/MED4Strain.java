package biological.cells;

import biological.components.Cytoplasm;
import biological.components.Gene;
import biological.components.Nucleoid;
import biological.components.PlasmaMembrane;
import biological.components.Thylakoid;
import biological.interfaces.GenomeProperties;
import biological.interfaces.Physiology;
import biological.util.CellConversion;
import java.util.*;

/**
 * MED4-specific implementation of Prochlorococcus marinus.
 */
public class MED4Strain extends Prochlorococcus {
    private final HighLightAdaptation highLightAdaptation;
    private final NutrientPhysiology nutrientPhysiology;
    
    public MED4Strain(String strain, double volumeMicron3, double dryFraction,
                    Cytoplasm cytoplasm, GenomeProperties genomeProperties,
                    Physiology physiology, PlasmaMembrane membrane, Thylakoid thylakoid) {
        super(strain, volumeMicron3, dryFraction, cytoplasm, genomeProperties, physiology, membrane, thylakoid);
        this.highLightAdaptation = new HighLightAdaptation();
        this.nutrientPhysiology = new NutrientPhysiology();
    }
    
    public static MED4Strain createFromGenes(List<Gene> genes, double volumeMicron3, double dryFraction) {
        Cytoplasm cytoplasm = createCytoplasm(genes, volumeMicron3);
        PlasmaMembrane membrane = createMembrane(volumeMicron3);
        Thylakoid thylakoid = createMED4Thylakoid();
        
        return new MED4Strain("MED4", volumeMicron3, dryFraction, cytoplasm,
                            new MED4GenomeProperties(), new MED4Physiology(),
                            membrane, thylakoid);
    }
    
    private static Cytoplasm createCytoplasm(List<Gene> genes, double volumeMicron3) {
        double cytoplasmVol = volumeMicron3 * 0.85;
        Nucleoid nucleoid = new Nucleoid(genes, "circular", new MED4GenomeProperties());
        PlasmaMembrane membrane = createMembrane(volumeMicron3);
        return new Cytoplasm(cytoplasmVol, nucleoid, membrane);
    }
    
    private static PlasmaMembrane createMembrane(double volumeMicron3) {
        double membraneVol = volumeMicron3 * 0.15;
        double surfaceArea = CellConversion.volumeToSurfaceArea(volumeMicron3);
        return new PlasmaMembrane(membraneVol, surfaceArea);
    }
    
    private static Thylakoid createMED4Thylakoid() {
        Thylakoid thylakoid = new Thylakoid(2);
        thylakoid.addPigment("divinyl chlorophyll a");
        thylakoid.addPigment("divinyl chlorophyll b");
        thylakoid.addPigment("zeaxanthin");
        thylakoid.addProtein("Photosystem I");
        thylakoid.addProtein("Photosystem II");
        thylakoid.addProtein("ATP synthase");
        return thylakoid;
    }
    
    @Override
    public double getGrowthRate() {
        double baseRate = physiology.getMaxGrowthRate();
        double sizeEffect = 1.0 / Math.sqrt(volumeMicron3);
        return baseRate * sizeEffect;
    }
    
    @Override
    public double simulatePhotosynthesis(double lightIntensity) {
        double maxRate = 2.0 * highLightAdaptation.getDivinylChlorophyllRatio();
        double halfSaturation = 100 * (1.0 + highLightAdaptation.getZeaxanthinContent());
        double photoinhibition = highLightAdaptation.calculatePhotoinhibition(lightIntensity);
        
        return maxRate * lightIntensity / (lightIntensity + halfSaturation) * (1 - photoinhibition);
    }
    
    @Override
    public Map<String, Double> getNutrientUptakeRates() {
        Map<String, Double> uptakeRates = new HashMap<>();
        double surfaceArea = getCytoplasm().getMembrane().getSurfaceArea();
        
        for (String nutrient : physiology.getNutrientRequirements().keySet()) {
            if (physiology.canUtilizeNutrient(nutrient)) {
                double quota = physiology.getNutrientRequirements().get(nutrient);
                uptakeRates.put(nutrient, surfaceArea * quota * 0.001);
            }
        }
        return uptakeRates;
    }
    
    // MED4-specific methods
    public double getDivinylChlorophyllRatio() {
        return highLightAdaptation.getDivinylChlorophyllRatio();
    }
    
    public boolean canUseNitrate() {
        return nutrientPhysiology.canUseNitrate();
    }
    
    public int getPsbACopyNumber() {
        return highLightAdaptation.getPsbACopyNumber();
    }
    
    // ===== MED4-SPECIFIC INNER CLASSES =====
    
    public static class MED4GenomeProperties implements GenomeProperties {
        private static final int EXPECTED_GENE_COUNT = 1716;
        private static final double GENOME_SIZE_MBP = 1.66;
        private static final String[] MISSING_GENES = {"narB", "glnA", "uvrA", "uvrB"};
        private static final String[] UNIQUE_GENES = {"psbA", "psbD", "highLightInducible"};
        private static final double GC_CONTENT = 0.307;
        
        @Override public int getExpectedGeneCount() { return EXPECTED_GENE_COUNT; }
        @Override public double getGenomeSizeMbp() { return GENOME_SIZE_MBP; }
        @Override public String[] getMissingGenes() { return MISSING_GENES; }
        @Override public String[] getUniqueGenes() { return UNIQUE_GENES; }
        @Override public boolean hasGene(String geneName) { return !Arrays.asList(MISSING_GENES).contains(geneName); }
        @Override public double getGCContent() { return GC_CONTENT; }
        @Override public String getGenomeStructure() { return "circular"; }
        @Override public double getOptimalTemperature() { return 24.0; }
        @Override public double getOptimalLight() { return 250.0; }
    }
    
    public static class MED4Physiology implements Physiology {
        private final Map<String, Double> nutrientQuotas;
        
        public MED4Physiology() {
            nutrientQuotas = new HashMap<>();
            nutrientQuotas.put("nitrogen", 0.05);
            nutrientQuotas.put("phosphorus", 0.003);
            nutrientQuotas.put("iron", 0.0005);
            nutrientQuotas.put("carbon", 1.0);
        }
        
        @Override public double getOptimalTemperature() { return 24.0; }
        @Override public double getOptimalpH() { return 7.2; }

        @Override
        public double getOptimalSalinity() {
            return 0.035;
        }
        private double maxGrowthRate = 1.8;
        public void setMaxGrowthRate(double maxGrowthRate) {
            this.maxGrowthRate = maxGrowthRate;
        }
        @Override
        public double getMaxGrowthRate() {
            return maxGrowthRate;
        }
        @Override public Map<String, Double> getNutrientRequirements() { return Collections.unmodifiableMap(nutrientQuotas); }
        @Override public Map<String, Double> getWasteProductionRates() { return Map.of("O2", 0.5, "organic_waste", 0.1); }
        
        @Override
        public double calculateEnvironmentalEffect(double temperature, double pH, double salinity, double oxygen, double light) {
            double tempEffect = 1.0 - Math.abs(temperature - 24.0) / 10.0;
            double lightEffect = light / (light + 250.0);
            double salinityEffect = 1.0 - Math.abs(salinity - 0.035) / 0.015;
            return Math.max(0, Math.min(1, tempEffect * lightEffect * salinityEffect));
        }
        
        @Override
        public boolean canUtilizeNutrient(String nutrient) {
            return switch (nutrient) {
                case "nitrate" -> false;
                case "nitrite" -> true;
                case "ammonium" -> true;
                case "urea" -> true;
                default -> false;
            };
        }
        
        @Override public boolean canTolerateStress(String stressType) { return stressType.equals("high_light"); }
        @Override public String getPrimaryEnergySource() { return "light"; }
        @Override public double getEnergyProductionRate() { return 120.0; }
        @Override public double getStressTolerance(String stressor) { return 0.8; }
        @Override public boolean canFormSpores() { return false; }

        @Override
        public java.util.Map<String, Double> getMonodConstants() {
            // Half-saturation constants calibrated to oligotrophic open-ocean conditions
            return java.util.Map.of(
                "nitrogen",   0.5,   // µM equivalent — adapted to low-N oligotrophic ocean
                "phosphorus", 0.03,  // µM equivalent — classic Redfield P limitation
                "iron",       0.001  // nM equivalent — iron often limits open-ocean cyanobacteria
            );
        }
    }
    
    public static class HighLightAdaptation {
        private final double divinylChlorophyllRatio;
        private final double zeaxanthinContent;
        private final int psbACopyNumber;
        
        public HighLightAdaptation() {
            this.divinylChlorophyllRatio = 0.9;
            this.zeaxanthinContent = 0.15;
            this.psbACopyNumber = 2;
        }
        
        public double getDivinylChlorophyllRatio() { return divinylChlorophyllRatio; }
        public double getZeaxanthinContent() { return zeaxanthinContent; }
        public int getPsbACopyNumber() { return psbACopyNumber; }
        
        public double calculatePhotoinhibition(double lightIntensity) {
            return Math.max(0, lightIntensity - 500) * 0.001;
        }
    }
    
    public static class NutrientPhysiology {
        private final boolean hasNitrateReductase;
        private final boolean hasNitriteReductase;

        public NutrientPhysiology() {
            this.hasNitrateReductase = false;
            this.hasNitriteReductase = true;
        }

        public boolean canUseNitrate() {
            return hasNitrateReductase;
        }

        public boolean canUseNitrite() {
            return hasNitriteReductase;
        }
    }
    
    @Override
    protected Map<String, Double> calculateATPProduction() {
        Map<String, Double> production = new HashMap<>();
        double photosynthesis = simulatePhotosynthesis(200); // Example light level
        production.put("photosynthesis", photosynthesis * 0.5); // Convert to ATP
        return production;
    }

    @Override
    protected Map<String, Double> calculateATPConsumption() {
        Map<String, Double> consumption = new HashMap<>();
        consumption.put("biosynthesis", getGrowthRate() * 1000); // Example
        consumption.put("maintenance", 50.0); // Example
        return consumption;
    }
}
