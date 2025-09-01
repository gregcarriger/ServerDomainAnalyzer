# Windows Server Domain Analysis Tool
# PowerShell script to run the Java program with full paths

Write-Host "Starting Windows Server Domain Analysis Tool..." -ForegroundColor Green
Write-Host ""

# Common Java installation paths to check
$javaLocations = @(
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\java.exe",
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot\bin\java.exe",
    "C:\Program Files\Eclipse Adoptium\jdk-11.0.24.8-hotspot\bin\java.exe",
    "C:\Program Files\Java\jdk-21\bin\java.exe",
    "C:\Program Files\Java\jdk-17\bin\java.exe",
    "C:\Program Files\Java\jdk-11\bin\java.exe",
    "C:\Program Files\Java\jdk1.8.0_*\bin\java.exe",
    "C:\Program Files (x86)\Eclipse Adoptium\jdk-21.0.8.9-hotspot\bin\java.exe",
    "C:\Program Files (x86)\Eclipse Adoptium\jdk-17.0.12.7-hotspot\bin\java.exe",
    "C:\Program Files (x86)\Eclipse Adoptium\jdk-11.0.24.8-hotspot\bin\java.exe",
    "C:\Program Files (x86)\Java\jdk-21\bin\java.exe",
    "C:\Program Files (x86)\Java\jdk-17\bin\java.exe",
    "C:\Program Files (x86)\Java\jdk-11\bin\java.exe"
)

# Function to find Java executable
function Find-JavaExecutable {
    # First check if java is in PATH
    try {
        $javaInPath = Get-Command java -ErrorAction Stop
        Write-Host "Found Java in PATH: $($javaInPath.Source)" -ForegroundColor Yellow
        return $javaInPath.Source
    }
    catch {
        Write-Host "Java not found in PATH, checking common installation locations..." -ForegroundColor Yellow
    }
    
    # Check common installation paths
    foreach ($location in $javaLocations) {
        # Handle wildcard paths
        if ($location -like "*`**") {
            $expandedPaths = Get-ChildItem -Path ($location -replace '\*.*$', '') -Directory -ErrorAction SilentlyContinue | 
                            ForEach-Object { Join-Path $_.FullName ($location -replace '.*\*', '') }
            foreach ($expandedPath in $expandedPaths) {
                if (Test-Path $expandedPath) {
                    Write-Host "Found Java at: $expandedPath" -ForegroundColor Yellow
                    return $expandedPath
                }
            }
        }
        else {
            if (Test-Path $location) {
                Write-Host "Found Java at: $location" -ForegroundColor Yellow
                return $location
            }
        }
    }
    
    return $null
}

# Find Java executable
$javaExe = Find-JavaExecutable

if (-not $javaExe) {
    Write-Host "ERROR: Java is not installed or not found in common locations" -ForegroundColor Red
    Write-Host "Please install Java 11 or higher from:" -ForegroundColor Red
    Write-Host "- Eclipse Adoptium: https://adoptium.net/" -ForegroundColor Red
    Write-Host "- Oracle: https://www.oracle.com/java/technologies/downloads/" -ForegroundColor Red
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

# Get current script directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$srcDir = Join-Path $scriptDir "src"
$outputDir = Join-Path $scriptDir "output"
$inputDir = Join-Path $scriptDir "input"
$defaultInputFile = Join-Path $inputDir "servers.txt"

# Check if source files exist
if (-not (Test-Path $srcDir)) {
    Write-Host "ERROR: Source directory not found: $srcDir" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Compile Java files if .class files don't exist
$classFile = Join-Path $srcDir "ServerDomainAnalyzer.class"
if (-not (Test-Path $classFile)) {
    Write-Host "Compiling Java files..." -ForegroundColor Yellow
    
    $javacExe = $javaExe -replace 'java\.exe$', 'javac.exe'
    $sourceFiles = Join-Path $srcDir "*.java"
    
    try {
        Start-Process -FilePath $javacExe -ArgumentList $sourceFiles -Wait -NoNewWindow
        Write-Host "Compilation successful." -ForegroundColor Green
        Write-Host ""
    }
    catch {
        Write-Host "ERROR: Compilation failed" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Create output directory if it doesn't exist
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    Write-Host "Created output directory: $outputDir" -ForegroundColor Yellow
}

# Prepare arguments for Java execution
$classPath = $srcDir
$mainClass = "ServerDomainAnalyzer"

# Determine input file
if ($args.Count -gt 0) {
    $inputFile = $args[0]
    if (-not (Test-Path $inputFile)) {
        Write-Host "ERROR: Input file not found: $inputFile" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} else {
    $inputFile = $defaultInputFile
    if (-not (Test-Path $inputFile)) {
        Write-Host "ERROR: Default input file not found: $inputFile" -ForegroundColor Red
        Write-Host "Please create the input file or specify a custom input file path." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Run the Java program
Write-Host "Running analysis with input file: $inputFile" -ForegroundColor Green
Write-Host ""

try {
    if ($args.Count -gt 0) {
        # Custom input file provided
        Start-Process -FilePath $javaExe -ArgumentList "-cp", $classPath, $mainClass, $inputFile -Wait -NoNewWindow
    } else {
        # Use default input file
        Start-Process -FilePath $javaExe -ArgumentList "-cp", $classPath, $mainClass -Wait -NoNewWindow
    }
    
    Write-Host ""
    Write-Host "Analysis complete! Check the output directory for results." -ForegroundColor Green
    Write-Host "Output directory: $outputDir" -ForegroundColor Yellow
} catch {
    Write-Host ""
    Write-Host "ERROR: Program execution failed" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Read-Host "Press Enter to exit"