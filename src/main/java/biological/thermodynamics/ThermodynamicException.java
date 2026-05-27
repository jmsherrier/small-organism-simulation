package biological.thermodynamics;

/**
 * Exception for thermodynamic violations in cellular simulations
 */
public class ThermodynamicException extends RuntimeException {
    public ThermodynamicException(String message) {
        super("Thermodynamic violation: " + message);
    }
}