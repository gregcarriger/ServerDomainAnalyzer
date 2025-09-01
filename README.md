# Windows Server Domain Analysis Tool

A Java program that analyzes Windows server names from a text file, calculates domain distribution percentages, and tracks historical data with timestamps.

## Features

- Parses server names to extract domain information (everything after the first dot)
- Calculates percentage distribution of servers across domains
- Tracks historical analysis results with timestamps in CSV format
- Handles servers without domains gracefully
- Provides detailed console output and CSV reporting

## Project Structure

```
ServerDomainAnalyzer/
├── src/
│   ├── ServerDomainAnalyzer.java    # Main application class
│   ├── DomainExtractor.java         # Domain parsing logic
│   ├── AnalysisResult.java          # Data model for results
│   └── CSVTracker.java              # CSV file operations
├── input/
│   └── servers.txt                  # Sample input file
├── output/
│   └── domain_history.csv           # Generated historical data
├── run.ps1                          # PowerShell script to run the program
└── README.md                        # This file
```

## Requirements

- Java 11 or higher
- Windows operating system

## Installation

1. Ensure Java is installed and available in your system PATH
2. Download or clone this project
3. Compile the Java files:
   ```
   javac src/*.java
   ```

## Usage

### Command Line

```bash
# Run with default input file (input/servers.txt)
java -cp src ServerDomainAnalyzer

# Run with custom input file
java -cp src ServerDomainAnalyzer path/to/your/servers.txt
```

### Using the PowerShell Script (Windows) - Recommended

```powershell
# Run with default input file
.\run.ps1

# Run with custom input file
.\run.ps1 "path/to/your/servers.txt"
```

## Input File Format

The input file should contain one server name per line:

```
# Comments start with # and are ignored
WEB01.contoso.com
DB-SRV01.contoso.com
MAIL01.fabrikam.local
APP-SERVER.external.org
STANDALONE-SRV        # Servers without domains are counted but not included in domain analysis
```

## Output

### Console Output

```
Server Domain Analysis - 2025-09-01T12:07:49.780
==========================================
Total Servers: 18
Servers with Domains: 16
Servers without Domains: 2

Domain Distribution:
- contoso.com        : 5 servers (27.8%)
- fabrikam.local     : 3 servers (16.7%)
- external.org       : 2 servers (11.1%)
- cloudapp.azure.com : 2 servers (11.1%)
- dev.local          : 1 server (5.6%)
- test.local         : 1 server (5.6%)
- sub.domain.example.com : 1 server (5.6%)
- microservices.internal : 1 server (5.6%)

Results saved to output/domain_history.csv
```

### CSV Output (domain_history.csv)

```csv
timestamp,domain,percentage,total_servers
2025-09-01T12:07:49.780,contoso.com,27.78,18
2025-09-01T12:07:49.780,fabrikam.local,16.67,18
2025-09-01T12:07:49.780,external.org,11.11,18
2025-09-01T12:07:49.780,cloudapp.azure.com,11.11,18
```

## Domain Extraction Logic

The program extracts domains using the following rules:

1. Finds the first dot (.) in the server name
2. Takes everything after the first dot as the domain
3. Converts domains to lowercase for consistency
4. Validates domain format (must contain valid characters and at least one dot)

### Examples:

- `WEB01.contoso.com` → `contoso.com`
- `DB-SRV.fabrikam.local` → `fabrikam.local`
- `API.sub.domain.example.com` → `sub.domain.example.com`
- `STANDALONE-SRV` → No domain (excluded from domain analysis)

## Error Handling

The program handles various error conditions:

- Missing or invalid input files
- Malformed server names
- CSV file write permissions
- Empty input files
- Invalid domain formats

## Historical Tracking

Each time you run the program, it appends new results to the CSV file with a timestamp. This allows you to:

- Track domain distribution changes over time
- Compare server inventory between different time periods
- Generate reports on domain migration trends
- Maintain audit trails of server infrastructure changes

## Customization

### Modifying Domain Extraction

To change how domains are extracted, modify the `extractDomain()` method in `DomainExtractor.java`.

### Changing Output Format

To modify the CSV output format, update the `toCsvString()` method in `AnalysisResult.java` and the CSV header in `CSVTracker.java`.

### Adding New Analysis Features

The modular design makes it easy to add new features:

- Extend `AnalysisResult` to include additional metrics
- Modify `ServerDomainAnalyzer` to perform additional calculations
- Update `CSVTracker` to handle new data fields

## Troubleshooting

### Java Not Found

**When using PowerShell script (run.ps1):**
The script automatically searches for Java in common installation locations. If Java is not found:

1. Download Java from [Eclipse Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Install Java 11 or higher
3. The script will automatically detect the installation

**When using Command Line or Batch file:**
If you get "java command not found" errors:

1. Verify Java is installed: Open Command Prompt and run `java -version`
2. If not installed, download Java from [Eclipse Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
3. Ensure Java is in your system PATH

### Compilation Errors

If compilation fails:

1. Ensure you're using Java 11 or higher
2. Check that all source files are present
3. Verify no syntax errors in the code

### File Permission Errors

If the program can't write to the output directory:

1. Run Command Prompt as Administrator
2. Check that the output directory is writable
3. Ensure no other programs have the CSV file open

## License

This project is provided as-is for educational and internal use purposes.
