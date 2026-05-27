package biological.components;

import java.util.*;

/**
 * Represents a gene with its name, function, and genomic coordinates.
 */
public class Gene {
    private String geneName;
    private String function;
    private List<int[]> ranges;
    private char strand;

    public Gene(String geneName, String function, int startBasePair, int endBasePair) {
        this(geneName, function, Collections.singletonList(new int[]{startBasePair, endBasePair}), '+');
    }

    public Gene(String geneName, String function, List<int[]> ranges, char strand) {
        this.geneName = geneName != null ? geneName : "unknown";
        this.function = function != null ? function : "unknown";
        this.ranges = new ArrayList<>(ranges);
        this.strand = (strand == '+' || strand == '-') ? strand : '+';
    }

    public String getGeneName() { return geneName; }
    public String getFunction() { return function; }
    public List<int[]> getRanges() { return Collections.unmodifiableList(ranges); }
    public char getStrand() { return strand; }

    public int getStartBasePair() { return ranges.get(0)[0]; }
    public int getEndBasePair() { return ranges.get(ranges.size() - 1)[1]; }

    public int getLength() {
        int length = 0;
        for (int[] range : ranges) {
            length += range[1] - range[0] + 1;
        }
        return length;
    }

    public Protein expressProtein(String location) {
        return new Protein(geneName, function, location);
    }
}
