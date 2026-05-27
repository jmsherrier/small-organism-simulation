package biological;

import biological.components.Gene;
import biological.util.CellConversion;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CellConversionTest {

    @Test
    void volumeToWetDaltons_usesCorrectDensity() {
        // 1 µm³ × 6.7×10¹¹ Da/µm³
        assertEquals(6.7e11, CellConversion.volumeToWetDaltons(1.0), 1e7);
    }

    @Test
    void volumeToWetDaltons_scalesLinearly() {
        double single = CellConversion.volumeToWetDaltons(1.0);
        assertEquals(single * 2, CellConversion.volumeToWetDaltons(2.0), 1.0);
    }

    @Test
    void volumeToDryDaltons_appliesDryFraction() {
        // 1 µm³ × 0.25 × 8.1×10¹¹ Da/µm³
        double expected = 0.25 * 8.1e11;
        assertEquals(expected, CellConversion.volumeToDryDaltons(1.0, 0.25), 1e7);
    }

    @Test
    void genomeToDaltons_singleGene() {
        // Gene from bp 1 to 100 → length=100, mass = 100 × 650 × 1.3 = 84,500 Da
        Gene gene = new Gene("testGene", "test", 1, 100);
        double mass = CellConversion.genomeToDaltons(List.of(gene));
        assertEquals(84_500.0, mass, 1.0);
    }

    @Test
    void genomeToDaltons_emptyList_returnsZero() {
        assertEquals(0.0, CellConversion.genomeToDaltons(List.of()));
    }
}
