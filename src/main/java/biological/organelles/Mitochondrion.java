package biological.organelles;

/**
 * Mitochondrion organelle implementation with ATP production
 */
public class Mitochondrion extends Organelle {
    private final double cristaeDensity;
    private final double oxidativeCapacity;
    
    public Mitochondrion(double volumeMicron3, double cristaeDensity) {
        super("Mitochondrion", volumeMicron3);
        this.cristaeDensity = cristaeDensity;
        this.oxidativeCapacity = volumeMicron3 * cristaeDensity * 150.0; // Increased scaling
    }
    
    @Override
    public double getFunctionalCapacity() {
        return volumeMicron3 * cristaeDensity;
    }
    
    public double calculateATPProduction(double oxygenConcentration) {
        // Michaelis-Menten kinetics for oxidative phosphorylation
        double maxRate = oxidativeCapacity;
        double km = 0.01; // Michaelis constant for O2 (approx 1%)
        double oxygenSaturation = oxygenConcentration / (oxygenConcentration + km);
        
        // Assume sufficient nutrients are available
        return maxRate * oxygenSaturation;
    }
    
    public double calculateATPProduction(double oxygenConcentration, double nutrientAvailability) {
        // More detailed version with nutrient limitation
        double maxRate = oxidativeCapacity;
        double oxygenSaturation = oxygenConcentration / (oxygenConcentration + 0.01);
        double nutrientSaturation = nutrientAvailability / (nutrientAvailability + 0.1);
        
        return maxRate * oxygenSaturation * nutrientSaturation;
    }
    
    public double getCristaeDensity() { return cristaeDensity; }
    public double getOxidativeCapacity() { return oxidativeCapacity; }
}
