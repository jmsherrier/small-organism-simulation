package biological.util;

import biological.components.Gene;
import java.util.List;

/**
 * Utility class for converting cell volumes to mass in Daltons with improved biological accuracy.
 */
public class CellConversion {
    private static final double BASE_PAIR_MASS_DA = 650;
    private static final double PROTEIN_AVG_MASS_DA = 40000;
    private static final double LIPID_AVG_MASS_DA = 750;
    private static final double CARBOHYDRATE_AVG_MASS_DA = 180;
    
    private static final double WET_DENSITY_FACTOR = 6.7e11;
    private static final double DRY_DENSITY_FACTOR = 8.1e11;

    public static double genomeToDaltons(List<Gene> genes) {
        if (genes == null || genes.isEmpty()) return 0;
        
        double total = 0;
        for (Gene gene : genes) {
            total += gene.getLength() * BASE_PAIR_MASS_DA;
        }
        return total * 1.3;
    }

    public static double volumeToWetDaltons(double volumeMicron3) {
        return volumeMicron3 * WET_DENSITY_FACTOR;
    }

    public static double volumeToDryDaltons(double volumeMicron3, double dryFraction) {
        return volumeMicron3 * dryFraction * DRY_DENSITY_FACTOR;
    }

    public static double estimateProteinMass(int proteinCount) {
        return proteinCount * PROTEIN_AVG_MASS_DA;
    }

    public static double estimateLipidMass(double membraneAreaNm2) {
        double lipidMolecules = (membraneAreaNm2 / 0.7) * 2;
        return lipidMolecules * LIPID_AVG_MASS_DA;
    }

    public static double volumeToSurfaceArea(double volumeMicron3) {
        double radius = Math.cbrt(volumeMicron3 * 0.75 / Math.PI);
        return 4 * Math.PI * radius * radius * 1e6;
    }
}
