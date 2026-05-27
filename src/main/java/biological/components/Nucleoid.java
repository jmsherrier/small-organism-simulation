package biological.components;

import biological.interfaces.GenomeProperties;
import biological.properties.DefaultGenomeProperties;
import java.util.List;

/**
 * Represents the nucleoid of a cell, containing a list of genes and genome structure.
 */
public class Nucleoid {
    private List<Gene> genes;
    private String structure;
    private GenomeProperties genomeProperties;

    public Nucleoid(List<Gene> genes, String structure, GenomeProperties genomeProperties) {
        this.genes = genes != null ? genes : List.of();
        this.structure = structure != null ? structure : "unknown";
        this.genomeProperties = genomeProperties != null ? genomeProperties : new DefaultGenomeProperties();
    }

    public Nucleoid(List<Gene> genes, String structure) {
        this(genes, structure, new DefaultGenomeProperties());
    }

    public List<Gene> getGenes() { return genes; }
    public String getStructure() { return structure; }
    public GenomeProperties getGenomeProperties() { return genomeProperties; }

    public double getGenomeMass() {
        return biological.util.CellConversion.genomeToDaltons(genes);
    }
}
