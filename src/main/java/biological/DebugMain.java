package biological;

import biological.cells.*;
import biological.components.Gene;
import biological.factory.CellFactory;
import biological.util.GenBankParser;
import biological.validation.ExperimentalValidator;
import java.io.IOException;
import java.util.List;

/**
 * Debug class to diagnose protein creation issues
 */
public class DebugMain {
    
    public static void main(String[] args) {
        System.out.println("=== DEBUG PROTEIN CREATION ===");
        
        try {
            // Load a small subset of genes for testing
            String med4File = "genbank_data/MED4.gb";
            List<Gene> med4Genes = GenBankParser.parseGenBankFile(med4File);
            
            System.out.println("MED4 genes loaded: " + med4Genes.size());
            
            // Create a test cell
            Cell med4Cell = CellFactory.createCell("photosynthetic", "MED4", med4Genes, 0.6, 0.3);
            
            // Debug protein counts
            debugProteinCounts(med4Cell, "MED4");
            
            // Test protein mass calculation - now using public method
            ExperimentalValidator validator = new ExperimentalValidator();
            double proteinMass = validator.calculateTotalProteinMass(med4Cell);
            double dryMass = med4Cell.getDryDaltonsWithGenome();
            double proteinFraction = proteinMass / dryMass;
            
            System.out.println("\n=== PROTEIN MASS CALCULATION ===");
            System.out.printf("Calculated protein mass: %.1e Da\n", proteinMass);
            System.out.printf("Total dry mass: %.1e Da\n", dryMass);
            System.out.printf("Protein fraction: %.3f\n", proteinFraction);
            System.out.printf("Expected protein fraction: 0.55\n");
            System.out.printf("Error: %.1f%%\n", Math.abs(proteinFraction - 0.55) / 0.55 * 100);
            
        } catch (IOException e) {
            System.err.println("Error loading GenBank file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void debugProteinCounts(Cell cell, String cellType) {
        System.out.println("\n=== " + cellType + " PROTEIN DEBUG ===");
        
        // Basic cell info
        System.out.println("Cell type: " + cellType);
        System.out.println("Total genes: " + cell.getCytoplasm().getNucleoid().getGenes().size());
        
        // Soluble proteins
        System.out.println("Soluble proteins: " + cell.getCytoplasm().getSolubleProteins().size());
        
        // Membrane proteins
        if (cell.getCytoplasm().getMembrane() != null) {
            System.out.println("Membrane proteins: " + cell.getCytoplasm().getMembrane().getMembraneProteins().size());
            
            // Debug membrane protein details
            if (!cell.getCytoplasm().getMembrane().getMembraneProteins().isEmpty()) {
                System.out.println("First membrane protein: " + 
                    cell.getCytoplasm().getMembrane().getMembraneProteins().get(0).getName());
            }
        }
        
        // Organelle proteins (for eukaryotic cells)
        if (cell instanceof EukaryoticCell) {
            EukaryoticCell eucCell = (EukaryoticCell) cell;
            int organelleProteins = 0;
            for (biological.organelles.Organelle organelle : eucCell.getOrganelles()) {
                organelleProteins += organelle.getProteins().size();
                if (!organelle.getProteins().isEmpty()) {
                    System.out.println(organelle.getName() + " proteins: " + organelle.getProteins().size());
                }
            }
            System.out.println("Total organelle proteins: " + organelleProteins);
        }
        
        // Check if any proteins exist at all
        int totalProteins = cell.getCytoplasm().getSolubleProteins().size() +
                          (cell.getCytoplasm().getMembrane() != null ? 
                           cell.getCytoplasm().getMembrane().getMembraneProteins().size() : 0);
        
        System.out.println("Total proteins detected: " + totalProteins);
        
        if (totalProteins == 0) {
            System.out.println("❌ WARNING: No proteins detected! Gene expression may not be working.");
            System.out.println("Check if Cytoplasm.expressAllGenes() is being called during cell creation.");
        } else if (totalProteins < 10) {
            System.out.println("⚠️  WARNING: Very few proteins detected (" + totalProteins + ")");
            System.out.println("Expected thousands of proteins for a functional cell.");
        } else {
            System.out.println("✅ Proteins detected: " + totalProteins);
        }
        
        System.out.println("==================================");
    }
}