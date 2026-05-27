package biological.components;

/**
 * Represents a simplified protein expressed from a gene.
 */
public class Protein {
    private final String name;
    private final String function;
    private final String location;

    public Protein(String name, String function, String location) {
        this.name = name;
        this.function = function;
        this.location = location;
    }

    public double catalyzeReaction(double substrate) {
        return substrate * 0.5;
    }

    public String getName() { return name; }
    public String getFunction() { return function; }
    public String getLocation() { return location; }
}
