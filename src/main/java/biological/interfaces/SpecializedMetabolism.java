package biological.interfaces;

/**
 * Interface for cells with specialized metabolism
 */
public interface SpecializedMetabolism {
    boolean canFixNitrogen();
    boolean canPerformRespiration();
    boolean canFerment();
    String getPrimaryEnergySource();
}
