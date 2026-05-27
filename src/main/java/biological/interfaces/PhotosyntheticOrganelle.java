package biological.interfaces;

/**
 * Interface for photosynthetic organelles
 */
public interface PhotosyntheticOrganelle {
    double performPhotosynthesis(double lightIntensity);
    double getPigmentContent(String pigment);
}
