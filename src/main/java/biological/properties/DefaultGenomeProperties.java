package biological.properties;

import biological.interfaces.GenomeProperties;

/**
 * Default genome properties implementation
 */
public class DefaultGenomeProperties implements GenomeProperties {
    @Override public int getExpectedGeneCount() { return 0; }
    @Override public double getGenomeSizeMbp() { return 0; }
    @Override public String[] getMissingGenes() { return new String[0]; }
    @Override public String[] getUniqueGenes() { return new String[0]; }
    @Override public boolean hasGene(String geneName) { return false; }
    @Override public double getGCContent() { return 0; }
    @Override public String getGenomeStructure() { return "unknown"; }
    @Override public double getOptimalTemperature() { return 0; }
    @Override public double getOptimalLight() { return 0; }
}
