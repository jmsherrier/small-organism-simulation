package biological.organelles;

import biological.components.Thylakoid;
import biological.interfaces.PhotosyntheticOrganelle;

/**
 * Chloroplast organelle implementation
 */
public class Chloroplast extends Organelle implements PhotosyntheticOrganelle {
    private final Thylakoid thylakoid;
    
    public Chloroplast(double volumeMicron3, Thylakoid thylakoid) {
        super("Chloroplast", volumeMicron3);
        this.thylakoid = thylakoid;
    }
    
    @Override
    public double getFunctionalCapacity() {
        return volumeMicron3 * thylakoid.getNumberOfLayers();
    }
    
    @Override
    public double performPhotosynthesis(double lightIntensity) {
        return getFunctionalCapacity() * lightIntensity * 0.8;
    }
    
    @Override
    public double getPigmentContent(String pigment) {
        return thylakoid.getPigments().contains(pigment) ? 1.0 : 0.0;
    }
    
    public Thylakoid getThylakoid() { return thylakoid; }
}
