package biological.properties;

import biological.interfaces.GenomeProperties;

/**
 * Genome properties for generic bacteria
 */
public class BacterialGenomeProperties implements GenomeProperties {
    @Override
    public int getExpectedGeneCount() { return 4000; }
    @Override
    public double getGenomeSizeMbp() { return 4.0; }
    @Override
    public String[] getMissingGenes() { return new String[0]; }
    @Override
    public String[] getUniqueGenes() { return new String[0]; }
    @Override
    public boolean hasGene(String geneName) { return true; }
    @Override
    public double getGCContent() { return 0.5; }
    @Override
    public String getGenomeStructure() { return "circular"; }
    @Override
    public double getOptimalTemperature() { return 37.0; }
    @Override
    public double getOptimalLight() { return 0.0; }
}
