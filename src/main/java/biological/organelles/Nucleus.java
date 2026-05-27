package biological.organelles;

import biological.components.Nucleoid;

/**
 * Nucleus organelle for eukaryotic cells
 */
public class Nucleus extends Organelle {
    private final Nucleoid nucleoid;
    
    public Nucleus(double volumeMicron3, Nucleoid nucleoid) {
        super("Nucleus", volumeMicron3);
        this.nucleoid = nucleoid;
    }
    
    @Override
    public double getFunctionalCapacity() {
        return 1.0; // Nucleus always functions at full capacity
    }
    
    public Nucleoid getNucleoid() { return nucleoid; }
    public double getDNADensity() {
        return nucleoid.getGenes().size() / volumeMicron3;
    }
}
