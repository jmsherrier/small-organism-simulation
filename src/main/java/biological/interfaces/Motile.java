package biological.interfaces;

/**
 * Interface for motile cells
 */
public interface Motile {
    boolean hasFlagella();
    double getSwimmingSpeed();
    double getChemotaxisEfficiency();
}
