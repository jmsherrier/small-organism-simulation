package biological.components;

/**
 * Represents a metabolic compound with chemical properties
 */
public class Metabolite {
    private final String name;
    private final String formula;
    private final double molecularWeight;
    private final double charge;
    
    public Metabolite(String name, String formula, double molecularWeight, double charge) {
        this.name = name;
        this.formula = formula;
        this.molecularWeight = molecularWeight;
        this.charge = charge;
    }
    
    // Getters
    public String getName() { return name; }
    public String getFormula() { return formula; }
    public double getMolecularWeight() { return molecularWeight; }
    public double getCharge() { return charge; }
    
    @Override
    public String toString() {
        return String.format("%s (%s, %.1f Da, charge: %.1f)", 
            name, formula, molecularWeight, charge);
    }
}