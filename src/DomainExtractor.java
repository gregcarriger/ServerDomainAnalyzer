/**
 * Utility class for extracting domain information from Windows server names
 * Extracts everything after the first dot in a server name
 */
public class DomainExtractor {
    
    /**
     * Extracts domain from a server name
     * Example: "SERVER01.contoso.com" -> "contoso.com"
     * 
     * @param serverName The full server name
     * @return Domain part of the server name, or null if no domain found
     */
    public String extractDomain(String serverName) {
        if (serverName == null || serverName.trim().isEmpty()) {
            return null;
        }
        
        // Remove any whitespace
        serverName = serverName.trim();
        
        // Find the first dot
        int firstDotIndex = serverName.indexOf('.');
        
        // If no dot found, server has no domain
        if (firstDotIndex == -1 || firstDotIndex == serverName.length() - 1) {
            return null;
        }
        
        // Extract everything after the first dot
        String domain = serverName.substring(firstDotIndex + 1);
        
        // Validate that domain is not empty and contains valid characters
        if (domain.isEmpty() || !isValidDomain(domain)) {
            return null;
        }
        
        return domain.toLowerCase(); // Normalize to lowercase for consistency
    }
    
    /**
     * Basic validation for domain format
     * Checks for valid characters and basic structure
     */
    private boolean isValidDomain(String domain) {
        if (domain == null || domain.isEmpty()) {
            return false;
        }
        
        // Basic validation: should contain only letters, numbers, dots, and hyphens
        // Should not start or end with dot or hyphen
        if (domain.startsWith(".") || domain.endsWith(".") || 
            domain.startsWith("-") || domain.endsWith("-")) {
            return false;
        }
        
        // Check for valid characters
        for (char c : domain.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '.' && c != '-') {
                return false;
            }
        }
        
        // Should contain at least one dot for a valid domain
        return domain.contains(".");
    }
    
    /**
     * Extracts the server hostname (part before the first dot)
     * 
     * @param serverName The full server name
     * @return Hostname part of the server name
     */
    public String extractHostname(String serverName) {
        if (serverName == null || serverName.trim().isEmpty()) {
            return null;
        }
        
        serverName = serverName.trim();
        int firstDotIndex = serverName.indexOf('.');
        
        if (firstDotIndex == -1) {
            return serverName; // No domain, return entire name as hostname
        }
        
        return serverName.substring(0, firstDotIndex);
    }
}