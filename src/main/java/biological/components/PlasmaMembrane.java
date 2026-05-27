package biological.components;

import java.util.*;

/**
 * Represents the plasma membrane with lipid composition and transport proteins.
 */
public class PlasmaMembrane {
    private final double volumeMicron3;
    private final double surfaceAreaNm2;
    private final Map<String, Double> lipidComposition;
    private final List<Protein> membraneProteins;

    public PlasmaMembrane(double volumeMicron3, double surfaceAreaNm2) {
        this.volumeMicron3 = volumeMicron3;
        this.surfaceAreaNm2 = surfaceAreaNm2;
        this.lipidComposition = new HashMap<>();
        this.membraneProteins = new ArrayList<>();
        initializeDefaultLipids();
    }

    private void initializeDefaultLipids() {
        lipidComposition.put("phosphatidylglycerol", 0.35);
        lipidComposition.put("phosphatidylethanolamine", 0.25);
        lipidComposition.put("cardiolipin", 0.15);
        lipidComposition.put("other", 0.25);
    }

    public void addMembraneProtein(Protein protein) {
        membraneProteins.add(protein);
    }

    public double getLipidMass() {
        return biological.util.CellConversion.estimateLipidMass(surfaceAreaNm2);
    }

    public double getProteinMass() {
        return membraneProteins.size() * 40000;
    }

    public double getTotalMass() {
        return getLipidMass() + getProteinMass();
    }

    public double getVolume() { return volumeMicron3; }
    public double getSurfaceArea() { return surfaceAreaNm2; }
    public Map<String, Double> getLipidComposition() { return Collections.unmodifiableMap(lipidComposition); }
    public List<Protein> getMembraneProteins() { return Collections.unmodifiableList(membraneProteins); }
}
