package biological.properties;

import biological.interfaces.GenomeProperties;

/**
 * Genome properties for eukaryotic cells
 */
public class EukaryoticGenomeProperties implements GenomeProperties {
    @Override
    public int getExpectedGeneCount() { return 20000; }
    @Override
    public double getGenomeSizeMbp() { return 100.0; }
    @Override
    public String[] getMissingGenes() { return new String[0]; } // FIXED: Removed extra parenthesis
    @Override
    public String[] getUniqueGenes() { return new String[]{"histones", "telomerase"}; }
    @Override
    public boolean hasGene(String geneName) { return true; }
    @Override
    public double getGCContent() { return 0.4; }
    @Override
    public String getGenomeStructure() { return "linear"; }
    @Override
    public double getOptimalTemperature() { return 25.0; }
    @Override
    public double getOptimalLight() { return 0.0; }
}
