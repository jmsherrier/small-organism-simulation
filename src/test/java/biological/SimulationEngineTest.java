package biological;

import biological.cells.Cell;
import biological.components.Gene;
import biological.factory.CellFactory;
import biological.simulation.SimulationEngine;
import biological.simulation.SimulationEnvironment;
import biological.simulation.SimulationResult;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SimulationEngineTest {

    // Minimal gene list — large enough that Cytoplasm.expressAllGenes() works but fast to construct
    private static List<Gene> minimalGenes(int count) {
        var genes = new java.util.ArrayList<Gene>(count);
        for (int i = 0; i < count; i++) {
            genes.add(new Gene("gene" + i, "hypothetical protein", i * 1000 + 1, i * 1000 + 300));
        }
        return genes;
    }

    @Test
    void med4Simulation_populationGrowsOverTime() {
        Cell cell = CellFactory.createCell("photosynthetic", "MED4", minimalGenes(10), 0.6, 0.3);
        SimulationEnvironment env = SimulationEnvironment.marinePhotic().build();

        SimulationResult result = new SimulationEngine().runSimulation(cell, env, 24.0);

        assertTrue(result.getFinalPopulation() > 1.0,
            "Population should grow from 1.0 over a 24 h marine photic simulation");
    }

    @Test
    void ecoliSimulation_populationGrowsOverTime() {
        Cell cell = CellFactory.createCell("heterotrophic", "E. coli", minimalGenes(10), 1.5, 0.25);
        SimulationEnvironment env = SimulationEnvironment.laboratoryAerobic().build();

        SimulationResult result = new SimulationEngine().runSimulation(cell, env, 12.0);

        assertTrue(result.getFinalPopulation() > 1.0,
            "E. coli population should grow under aerobic lab conditions");
    }

    @Test
    void yeastSimulation_populationGrowsOverTime() {
        Cell cell = CellFactory.createCell("eukaryotic", "Yeast", minimalGenes(10), 30.0, 0.28);
        SimulationEnvironment env = SimulationEnvironment.yeastFermentation().build();

        SimulationResult result = new SimulationEngine().runSimulation(cell, env, 24.0);

        assertTrue(result.getFinalPopulation() > 1.0,
            "Yeast population should grow under fermentation conditions");
    }

    @Test
    void simulation_recordsCorrectNumberOfTimePoints() {
        Cell cell = CellFactory.createCell("heterotrophic", "E. coli", minimalGenes(5), 1.5, 0.25);
        SimulationEnvironment env = SimulationEnvironment.laboratoryAerobic().build();

        // 12 h ÷ 0.25 h/step = 48 steps + 1 (inclusive) = 49 data points
        SimulationResult result = new SimulationEngine().runSimulation(cell, env, 12.0);

        assertEquals(49, result.getTimePoints().size());
    }
}
