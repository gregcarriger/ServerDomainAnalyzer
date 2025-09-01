import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Handles CSV file operations for tracking historical domain analysis data
 * Manages reading from and writing to the domain history CSV file
 */
public class CSVTracker {
    private static final String CSV_HEADER = "timestamp,domain,percentage,total_servers";
    
    /**
     * Saves analysis results to CSV file
     * Creates the file and directories if they don't exist
     * Appends new results to existing file
     * 
     * @param filePath Path to the CSV file
     * @param results List of analysis results to save
     * @throws IOException If file operations fail
     */
    public void saveResults(String filePath, List<AnalysisResult> results) throws IOException {
        Path path = Paths.get(filePath);
        
        // Create directories if they don't exist
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        boolean fileExists = Files.exists(path);
        boolean needsHeader = !fileExists || Files.size(path) == 0;
        
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(filePath, true))) { // Append mode
            
            // Write header if file is new or empty
            if (needsHeader) {
                writer.println(CSV_HEADER);
            }
            
            // Write each result
            for (AnalysisResult result : results) {
                writer.println(result.toCsvString());
            }
        }
    }
    
    /**
     * Reads existing analysis results from CSV file
     * Returns empty list if file doesn't exist
     * 
     * @param filePath Path to the CSV file
     * @return List of analysis results from the file
     * @throws IOException If file reading fails
     */
    public List<AnalysisResult> readResults(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            return List.of(); // Return empty list if file doesn't exist
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.lines()
                .skip(1) // Skip header
                .filter(line -> !line.trim().isEmpty())
                .map(this::parseCSVLine)
                .filter(result -> result != null) // Filter out invalid lines
                .toList();
        }
    }
    
    /**
     * Parses a single CSV line into an AnalysisResult
     * 
     * @param csvLine CSV line to parse
     * @return AnalysisResult object or null if parsing fails
     */
    private AnalysisResult parseCSVLine(String csvLine) {
        try {
            String[] parts = csvLine.split(",");
            if (parts.length >= 4) {
                String timestamp = parts[0].trim();
                String domain = parts[1].trim();
                double percentage = Double.parseDouble(parts[2].trim());
                int totalServers = Integer.parseInt(parts[3].trim());
                
                // Calculate server count from percentage and total
                int serverCount = (int) Math.round((percentage * totalServers) / 100.0);
                
                return new AnalysisResult(timestamp, domain, percentage, totalServers, serverCount);
            }
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse CSV line: " + csvLine);
        }
        return null;
    }
    
    /**
     * Checks if the CSV file exists and is readable
     * 
     * @param filePath Path to check
     * @return true if file exists and is readable
     */
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isReadable(path);
    }
    
    /**
     * Gets the number of records in the CSV file (excluding header)
     * 
     * @param filePath Path to the CSV file
     * @return Number of data records in the file
     * @throws IOException If file reading fails
     */
    public long getRecordCount(String filePath) throws IOException {
        if (!fileExists(filePath)) {
            return 0;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            return reader.lines()
                .skip(1) // Skip header
                .filter(line -> !line.trim().isEmpty())
                .count();
        }
    }
    
    /**
     * Creates a backup of the CSV file
     * 
     * @param filePath Original file path
     * @param backupPath Backup file path
     * @throws IOException If backup operation fails
     */
    public void createBackup(String filePath, String backupPath) throws IOException {
        Path source = Paths.get(filePath);
        Path backup = Paths.get(backupPath);
        
        if (Files.exists(source)) {
            // Create backup directory if it doesn't exist
            Path backupDir = backup.getParent();
            if (backupDir != null && !Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            Files.copy(source, backup, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}