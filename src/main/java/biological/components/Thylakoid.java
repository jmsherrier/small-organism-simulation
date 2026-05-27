package biological.components;

import java.util.*;

/**
 * Represents thylakoid membranes inside a photosynthetic cell.
 */
public class Thylakoid {
    private final int numberOfLayers;
    private final List<String> proteins;
    private final List<String> pigments;

    public Thylakoid(int numberOfLayers) {
        this.numberOfLayers = numberOfLayers;
        this.proteins = new ArrayList<>();
        this.pigments = new ArrayList<>();
    }

    public int getNumberOfLayers() { return numberOfLayers; }
    public void addProtein(String protein) { proteins.add(protein); }
    public void addPigment(String pigment) { pigments.add(pigment); }
    public List<String> getProteins() { return Collections.unmodifiableList(proteins); }
    public List<String> getPigments() { return Collections.unmodifiableList(pigments); }
    public boolean containsPigment(String pigment) { return pigments.contains(pigment); }
}
