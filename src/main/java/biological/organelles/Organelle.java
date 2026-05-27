package biological.organelles;

import biological.components.Protein;
import java.util.*;

/**
 * Base class for cellular organelles
 */
public abstract class Organelle {
    protected final String name;
    protected final double volumeMicron3;
    protected final List<Protein> proteins;
    
    public Organelle(String name, double volumeMicron3) {
        this.name = name;
        this.volumeMicron3 = volumeMicron3;
        this.proteins = new ArrayList<>();
    }
    
    public abstract double getFunctionalCapacity();
    
    public void addProtein(Protein protein) {
        proteins.add(protein);
    }
    
    public String getName() { return name; }
    public double getVolumeMicron3() { return volumeMicron3; }
    public List<Protein> getProteins() { return Collections.unmodifiableList(proteins); }
}
