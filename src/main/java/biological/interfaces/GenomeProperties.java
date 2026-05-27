package biological.interfaces;

/**
 * Interface defining genome properties for different cell types
 */
public interface GenomeProperties {
    int getExpectedGeneCount();
    double getGenomeSizeMbp();
    String[] getMissingGenes();
    String[] getUniqueGenes();
    boolean hasGene(String geneName);
    double getGCContent();
    String getGenomeStructure();
    double getOptimalTemperature();
    double getOptimalLight();
}
