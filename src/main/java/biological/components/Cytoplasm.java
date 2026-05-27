package biological.components;

import java.util.*;

/**
 * Represents the cytoplasm with realistic molecular composition.
 */
public class Cytoplasm {
    private final double volumeMicron3;
    private final Nucleoid nucleoid;
    private final PlasmaMembrane membrane;
    private final List<Protein> solubleProteins;
    private final Map<String, Double> metabolites;
    private final double pH;
    private final double ionicStrength;

    public Cytoplasm(double volumeMicron3, Nucleoid nucleoid, PlasmaMembrane membrane) {
        this.volumeMicron3 = volumeMicron3;
        this.nucleoid = nucleoid;
        this.membrane = membrane;
        this.solubleProteins = new ArrayList<>();
        this.metabolites = new HashMap<>();
        this.pH = 7.2;
        this.ionicStrength = 0.15;
        
        expressAllGenes();
        initializeMetabolites();
    }

    private void expressAllGenes() {
        for (Gene g : nucleoid.getGenes()) {
            Protein protein = g.expressProtein("cytoplasm");
            solubleProteins.add(protein);
        }
    }

    private void initializeMetabolites() {
        metabolites.put("ATP", 3.0);
        metabolites.put("ADP", 0.8);
        metabolites.put("NADH", 0.3);
        metabolites.put("glucose", 5.0);
        metabolites.put("amino_acids", 15.0);
    }

    public void simulateActivity() {
        double totalEffect = 0;
        double crowdingFactor = calculateCrowdingFactor();
        
        for (Protein p : solubleProteins) {
            double reactionRate = p.catalyzeReaction(1.0) * crowdingFactor * pHEffect(pH);
            totalEffect += reactionRate;
        }
        
        System.out.printf("Total cytoplasmic activity: %.2f (crowding: %.2f, pH effect: %.2f)%n", 
                         totalEffect, crowdingFactor, pHEffect(pH));
    }

    private double calculateCrowdingFactor() {
        double proteinConcentration = solubleProteins.size() * 40000 / (volumeMicron3 * 1e-15 * 6.022e23);
        return 1.0 / (1.0 + proteinConcentration / 0.3);
    }

    private double pHEffect(double pH) {
        return Math.exp(-Math.pow(pH - 7.2, 2) / 2.0);
    }

    public double getOsmolarity() {
        return ionicStrength * 1000 + metabolites.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // Getters
    public Nucleoid getNucleoid() { return nucleoid; }
    public List<Protein> getSolubleProteins() { return Collections.unmodifiableList(solubleProteins); }
    public double getVolume() { return volumeMicron3; }
    public PlasmaMembrane getMembrane() { return membrane; }
    public double getpH() { return pH; }
    public Map<String, Double> getMetabolites() { return Collections.unmodifiableMap(metabolites); }
}
