import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main class for analyzing Windows server domain distribution
 * Reads server names from a text file, calculates domain percentages,
 * and tracks historical data with timestamps
 */
public class ServerDomainAnalyzer {
    private static final String DEFAULT_INPUT_FILE = "input/servers.txt";
    private static final String OUTPUT_CSV = "output/domain_history.csv";
    
    public static void main(String[] args) {
        String inputFile = args.length > 0 ? args[0] : DEFAULT_INPUT_FILE;
        
        try {
            // Read server names from file
            List<String> serverNames = readServerNames(inputFile);
            if (serverNames.isEmpty()) {
                System.out.println("No server names found in file: " + inputFile);
                return;
            }
            
            // Extract domains and calculate distribution
            DomainExtractor extractor = new DomainExtractor();
            Map<String, Integer> domainCounts = new HashMap<>();
            int totalServers = 0;
            int serversWithDomains = 0;
            
            for (String serverName : serverNames) {
                totalServers++;
                String domain = extractor.extractDomain(serverName);
                if (domain != null && !domain.isEmpty()) {
                    domainCounts.put(domain, domainCounts.getOrDefault(domain, 0) + 1);
                    serversWithDomains++;
                }
            }
            
            // Generate timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            // Create analysis results
            List<AnalysisResult> results = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : domainCounts.entrySet()) {
                String domain = entry.getKey();
                int count = entry.getValue();
                double percentage = (count * 100.0) / totalServers;
                results.add(new AnalysisResult(timestamp, domain, percentage, totalServers, count));
            }
            
            // Sort results by percentage (descending)
            results.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));
            
            // Display console output
            displayResults(results, totalServers, serversWithDomains);
            
            // Save to CSV
            CSVTracker csvTracker = new CSVTracker();
            csvTracker.saveResults(OUTPUT_CSV, results);
            
            System.out.println("\nResults saved to " + OUTPUT_CSV);
            
        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Reads server names from a text file (one per line)
     */
    private static List<String> readServerNames(String filePath) throws IOException {
        List<String> serverNames = new ArrayList<>();
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Input file not found: " + filePath);
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) { // Skip empty lines and comments
                    serverNames.add(line);
                }
            }
        }
        
        return serverNames;
    }
    
    /**
     * Displays analysis results to console
     */
    private static void displayResults(List<AnalysisResult> results, int totalServers, int serversWithDomains) {
        System.out.println("\nServer Domain Analysis - " + 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println("==========================================");
        System.out.println("Total Servers: " + totalServers);
        System.out.println("Servers with Domains: " + serversWithDomains);
        
        if (totalServers != serversWithDomains) {
            System.out.println("Servers without Domains: " + (totalServers - serversWithDomains));
        }
        
        System.out.println("\nDomain Distribution:");
        
        for (AnalysisResult result : results) {
            System.out.printf("- %-20s: %d servers (%.1f%%)\n", 
                result.getDomain(), result.getServerCount(), result.getPercentage());
        }
    }
}