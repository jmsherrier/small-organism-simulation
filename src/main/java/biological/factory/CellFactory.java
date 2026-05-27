package biological.factory;

import biological.cells.*;
import biological.components.*;
import biological.organelles.*;
import biological.properties.*;
import biological.util.CellConversion;
import biological.validation.ExperimentalValidator;
import java.util.*;

/**
 * Factory for creating different cell types with proper biological constants
 */
public class CellFactory {
    
    private static final ExperimentalValidator validator = new ExperimentalValidator();
    
    public static Cell createCell(String cellType, String strain, List<Gene> genes, 
                                double volume, double dryFraction) {
        switch (cellType.toLowerCase()) {
            case "photosynthetic":
                return createPhotosyntheticCell(strain, genes, volume, dryFraction);
            case "heterotrophic":
                return createHeterotrophicCell(strain, genes, volume, dryFraction);
            case "eukaryotic":
                return createEukaryoticCell(strain, genes, volume, dryFraction);
            default:
                throw new IllegalArgumentException("Unknown cell type: " + cellType);
        }
    }
    
    private static Cell createPhotosyntheticCell(String strain, List<Gene> genes,
                                               double volume, double dryFraction) {
        
        double cytoplasmVol = volume * 0.85;
        double membraneVol = volume * 0.15;
        double surfaceArea = CellConversion.volumeToSurfaceArea(volume);
        
        Nucleoid nucleoid = new Nucleoid(genes, "circular", new biological.cells.MED4Strain.MED4GenomeProperties());
        PlasmaMembrane membrane = new PlasmaMembrane(membraneVol, surfaceArea);
        
        Cytoplasm cytoplasm = new Cytoplasm(cytoplasmVol, nucleoid, membrane);
        addEssentialProteins(membrane);
        
        Thylakoid thylakoid = createThylakoid();
        
        return new MED4Strain(strain, volume, dryFraction, cytoplasm,
                            new biological.cells.MED4Strain.MED4GenomeProperties(), 
                            new biological.cells.MED4Strain.MED4Physiology(),
                            membrane, thylakoid);
    }
    
    private static Cell createHeterotrophicCell(String strain, List<Gene> genes,
                                              double volume, double dryFraction) {
        
        double cytoplasmVol = volume * 0.85;
        double membraneVol = volume * 0.15;
        double surfaceArea = CellConversion.volumeToSurfaceArea(volume);
        
        Nucleoid nucleoid = new Nucleoid(genes, "circular", new BacterialGenomeProperties());
        PlasmaMembrane membrane = new PlasmaMembrane(membraneVol, surfaceArea);
        
        Cytoplasm cytoplasm = new Cytoplasm(cytoplasmVol, nucleoid, membrane);
        addEssentialProteins(membrane);
        
        RespirationProperties respiration = new RespirationProperties(true, false, 0.8, "oxygen");
        
        return new HeterotrophicBacterium(strain, volume, dryFraction, cytoplasm,
                                        new BacterialGenomeProperties(), new BacterialPhysiology(),
                                        membrane, respiration);
    }
    
    private static Cell createEukaryoticCell(String strain, List<Gene> genes,
                                        double volume, double dryFraction) {
        
        double cytoplasmVol = volume * 0.7;
        double membraneVol = volume * 0.1;
        double surfaceArea = CellConversion.volumeToSurfaceArea(volume);
        
        Nucleoid nucleoid = new Nucleoid(genes, "linear", new EukaryoticGenomeProperties());
        PlasmaMembrane membrane = new PlasmaMembrane(membraneVol, surfaceArea);
        
        Cytoplasm cytoplasm = new Cytoplasm(cytoplasmVol, nucleoid, membrane);
        addEssentialProteins(membrane);
        
        Nucleus nucleus = new Nucleus(volume * 0.1, nucleoid);
        Mitochondrion mitochondrion1 = new Mitochondrion(volume * 0.03, 2.5);
        Mitochondrion mitochondrion2 = new Mitochondrion(volume * 0.02, 2.2);
        
        addOrganelleProteins(nucleus);
        addOrganelleProteins(mitochondrion1);
        addOrganelleProteins(mitochondrion2);
        
        List<Organelle> organelles = Arrays.asList(nucleus, mitochondrion1, mitochondrion2);
        
        return new EukaryoticCell(strain, volume, dryFraction, cytoplasm,
                                new EukaryoticGenomeProperties(), new EukaryoticPhysiology(),
                                membrane, nucleus, organelles);
    }
    
    private static void addEssentialProteins(PlasmaMembrane membrane) {
        membrane.addMembraneProtein(new Protein("ATP_synthase", "Energy production", "membrane"));
        membrane.addMembraneProtein(new Protein("Transport_protein", "Nutrient transport", "membrane"));
        membrane.addMembraneProtein(new Protein("Receptor_protein", "Signal transduction", "membrane"));
    }
    
    private static void addOrganelleProteins(Organelle organelle) {
        organelle.addProtein(new Protein(organelle.getName() + "_enzyme", "Metabolic function", organelle.getName()));
        organelle.addProtein(new Protein(organelle.getName() + "_structural", "Structural support", organelle.getName()));
    }
    
    private static Thylakoid createThylakoid() {
        Thylakoid thylakoid = new Thylakoid(2);
        thylakoid.addPigment("divinyl chlorophyll a");
        thylakoid.addPigment("divinyl chlorophyll b");
        thylakoid.addPigment("zeaxanthin");
        thylakoid.addProtein("Photosystem I");
        thylakoid.addProtein("Photosystem II");
        thylakoid.addProtein("ATP synthase");
        return thylakoid;
    }
    
    // Public method to get biological constants
    public static double getProteinFraction(String strain) {
        return validator.getProteinFraction(strain);
    }
}
