/**
 * Data class to hold the results of domain analysis
 * Contains timestamp, domain name, percentage, and server counts
 */
public class AnalysisResult {
    private String timestamp;
    private String domain;
    private double percentage;
    private int totalServers;
    private int serverCount;
    
    /**
     * Constructor for AnalysisResult
     * 
     * @param timestamp When the analysis was performed
     * @param domain The domain name
     * @param percentage Percentage of servers in this domain
     * @param totalServers Total number of servers analyzed
     * @param serverCount Number of servers in this specific domain
     */
    public AnalysisResult(String timestamp, String domain, double percentage, 
                         int totalServers, int serverCount) {
        this.timestamp = timestamp;
        this.domain = domain;
        this.percentage = percentage;
        this.totalServers = totalServers;
        this.serverCount = serverCount;
    }
    
    // Getters
    public String getTimestamp() {
        return timestamp;
    }
    
    public String getDomain() {
        return domain;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public int getTotalServers() {
        return totalServers;
    }
    
    public int getServerCount() {
        return serverCount;
    }
    
    // Setters
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public void setTotalServers(int totalServers) {
        this.totalServers = totalServers;
    }
    
    public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
    }
    
    /**
     * Returns CSV formatted string for this result
     * Format: timestamp,domain,percentage,total_servers
     */
    public String toCsvString() {
        return String.format("%s,%s,%.2f,%d", 
            timestamp, domain, percentage, totalServers);
    }
    
    /**
     * Returns a formatted string representation of this result
     */
    @Override
    public String toString() {
        return String.format("AnalysisResult{timestamp='%s', domain='%s', " +
                           "percentage=%.2f%%, totalServers=%d, serverCount=%d}", 
                           timestamp, domain, percentage, totalServers, serverCount);
    }
    
    /**
     * Checks equality based on timestamp and domain
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        AnalysisResult that = (AnalysisResult) obj;
        return timestamp.equals(that.timestamp) && domain.equals(that.domain);
    }
    
    /**
     * Hash code based on timestamp and domain
     */
    @Override
    public int hashCode() {
        return timestamp.hashCode() * 31 + domain.hashCode();
    }
}