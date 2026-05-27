package biological.interfaces;

import biological.components.Thylakoid;

/**
 * Interface for photosynthetic cells
 */
public interface Photosynthetic {
    Thylakoid getThylakoid();
    double simulatePhotosynthesis(double lightIntensity);
    double getPigmentContent(String pigment);
}
