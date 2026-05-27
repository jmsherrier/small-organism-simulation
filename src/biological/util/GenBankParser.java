package biological.util;

import biological.components.Gene;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Parses GenBank flat files to extract gene information and genome structure.
 */
public class GenBankParser {

    public static List<Gene> parseGenBankFile(String filePath) throws IOException {
        List<Gene> genes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            
            String geneName = null;
            String function = null;
            List<int[]> ranges = null;
            char strand = '+';
            boolean inCDS = false;
            boolean collectingProduct = false;
            StringBuilder productBuilder = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                
                if (trimmed.startsWith("CDS")) {
                    if (inCDS && geneName != null && ranges != null) {
                        genes.add(new Gene(geneName, function != null ? function : "unknown", ranges, strand));
                    }
                    
                    inCDS = true;
                    geneName = null;
                    function = null;
                    productBuilder.setLength(0);
                    ranges = new ArrayList<>();
                    strand = '+';
                    
                    String coordPart = trimmed.substring(3).trim();
                    
                    if (coordPart.startsWith("complement(")) {
                        strand = '-';
                        coordPart = coordPart.substring(11, coordPart.length() - 1);
                    }
                    if (coordPart.startsWith("join(")) {
                        coordPart = coordPart.substring(5, coordPart.length() - 1);
                    }
                    
                    for (String p : coordPart.split(",")) {
                        Matcher m = Pattern.compile("(\\d+)\\.\\.(\\d+)").matcher(p.trim());
                        if (m.find()) {
                            ranges.add(new int[]{Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))});
                        }
                    }
                }
                
                if (!inCDS) continue;
                
                if (trimmed.startsWith("/gene=")) geneName = trimmed.substring(6).replace("\"", "").trim();
                else if (trimmed.startsWith("/locus_tag=") && (geneName == null || geneName.isEmpty()))
                    geneName = trimmed.substring(11).replace("\"", "").trim();
                
                if (trimmed.startsWith("/product=")) {
                    collectingProduct = true;
                    productBuilder.setLength(0);
                    String value = trimmed.substring(9).replace("\"", "").trim();
                    productBuilder.append(value);
                    if (trimmed.endsWith("\"")) {
                        collectingProduct = false;
                        function = productBuilder.toString().trim();
                    } else productBuilder.append(" ");
                } else if (collectingProduct) {
                    String value = trimmed.replace("\"", "").trim();
                    productBuilder.append(value);
                    if (trimmed.endsWith("\"")) {
                        collectingProduct = false;
                        function = productBuilder.toString().trim();
                    } else productBuilder.append(" ");
                }
                
                if (trimmed.isEmpty() && inCDS) {
                    if (geneName != null && ranges != null)
                        genes.add(new Gene(geneName, function != null ? function : "unknown", ranges, strand));
                    inCDS = false;
                }
            }
            
            if (inCDS && geneName != null && ranges != null)
                genes.add(new Gene(geneName, function != null ? function : "unknown", ranges, strand));
        }
        return genes;
    }

    public static String getGenomeStructure(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("LOCUS")) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length >= 6) {
                        String structure = tokens[5].toLowerCase();
                        if (structure.equals("circular") || structure.equals("linear")) return structure;
                    }
                    return "unknown";
                }
            }
        }
        return "unknown";
    }

    public static void downloadGenBankFile(String accession, String outputPath) throws IOException {
        String url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&id=" +
                accession + "&rettype=gb&retmode=text";

        try (InputStream in = java.net.URI.create(url).toURL().openStream();
             FileOutputStream fos = new FileOutputStream(outputPath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) fos.write(buffer, 0, bytesRead);
        }
    }
}