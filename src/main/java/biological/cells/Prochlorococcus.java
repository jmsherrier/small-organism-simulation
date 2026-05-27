 package biological.cells;

import biological.components.Cytoplasm;
import biological.components.PlasmaMembrane;
import biological.components.Thylakoid;
import biological.interfaces.GenomeProperties;
import biological.interfaces.Photosynthetic;
import biological.interfaces.Physiology;
import java.util.Map;

/**
 * Generalized Prochlorococcus base class implementing Photosynthetic interface
 */
public abstract class Prochlorococcus extends Cell implements Photosynthetic {
    protected final Thylakoid thylakoid;
    
    public Prochlorococcus(String strain, double volumeMicron3, double dryFraction,
                         Cytoplasm cytoplasm, GenomeProperties genomeProperties, 
                         Physiology physiology, PlasmaMembrane membrane, Thylakoid thylakoid) {
        super(strain, volumeMicron3, dryFraction, cytoplasm, genomeProperties, physiology, membrane);
        this.thylakoid = thylakoid;
    }
    
    @Override
    public Thylakoid getThylakoid() { return thylakoid; }
    
    @Override
    public double getPigmentContent(String pigment) {
        return thylakoid.getPigments().contains(pigment) ? 1.0 : 0.0;
    }
    
    // Abstract methods to be implemented by specific strains
    @Override
    public abstract double getGrowthRate();
    @Override
    public abstract Map<String, Double> getNutrientUptakeRates();
    @Override
    public abstract double simulatePhotosynthesis(double lightIntensity);
}
