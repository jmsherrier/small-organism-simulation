package biological;

import biological.components.Gene;
import biological.util.GenBankParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GenBankParserTest {

    // Minimal GenBank FEATURES section with two CDS entries
    private static final String MINI_GENBANK = """
            FEATURES             Location/Qualifiers
                 CDS             100..300
                                 /gene="geneA"
                                 /product="Protein A"
                 CDS             complement(500..700)
                                 /gene="geneB"
                                 /product="Protein B"
            ORIGIN
            //
            """;

    @Test
    void parsesCorrectNumberOfGenes(@TempDir Path tempDir) throws IOException {
        Path gbFile = tempDir.resolve("test.gb");
        Files.writeString(gbFile, MINI_GENBANK);

        List<Gene> genes = GenBankParser.parseGenBankFile(gbFile.toString());
        assertEquals(2, genes.size());
    }

    @Test
    void parsesGeneNamesCorrectly(@TempDir Path tempDir) throws IOException {
        Path gbFile = tempDir.resolve("test.gb");
        Files.writeString(gbFile, MINI_GENBANK);

        List<Gene> genes = GenBankParser.parseGenBankFile(gbFile.toString());
        assertTrue(genes.stream().anyMatch(g -> g.getGeneName().equals("geneA")));
        assertTrue(genes.stream().anyMatch(g -> g.getGeneName().equals("geneB")));
    }

    @Test
    void parsesComplementStrand(@TempDir Path tempDir) throws IOException {
        Path gbFile = tempDir.resolve("test.gb");
        Files.writeString(gbFile, MINI_GENBANK);

        List<Gene> genes = GenBankParser.parseGenBankFile(gbFile.toString());
        assertTrue(genes.stream().anyMatch(g -> g.getStrand() == '-'));
    }

    @Test
    void computesGeneLengthFromCoordinates(@TempDir Path tempDir) throws IOException {
        Path gbFile = tempDir.resolve("test.gb");
        Files.writeString(gbFile, MINI_GENBANK);

        List<Gene> genes = GenBankParser.parseGenBankFile(gbFile.toString());
        // geneA: 100..300 → length = 201
        Gene geneA = genes.stream().filter(g -> g.getGeneName().equals("geneA")).findFirst().orElseThrow();
        assertEquals(201, geneA.getLength());
    }
}
