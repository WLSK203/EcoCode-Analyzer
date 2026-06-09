# EcoCode Analyzer

> **Carbon Footprint Analysis for Source Code**  
> Detect algorithmic inefficiencies. Estimate carbon emissions. Write greener software.

**Created by [Alok Sharma](https://www.linkedin.com/in/alok-sharma-b17550321/)**

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [How It Works](#how-it-works)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Build](#build)
  - [Run](#run)
- [Usage](#usage)
  - [Interactive Mode](#interactive-mode)
  - [Command-Line Mode](#command-line-mode)
  - [Demo Mode](#demo-mode)
- [Sample Output](#sample-output)
- [Algorithm Comparison](#algorithm-comparison)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Carbon Calculation Model](#carbon-calculation-model)
- [Testing](#testing)
- [Future Roadmap](#future-roadmap)
- [License](#license)

---

## Overview

EcoCode Analyzer is a Java-based command-line tool that statically analyzes source code to detect algorithmic complexity, estimate CPU energy consumption, and calculate the resulting carbon dioxide emissions — all without executing the code.

It is built on the IEEE research model for software energy estimation and targets developers who want to understand and reduce the environmental cost of their code.


**Domain:** Sustainable Software Engineering / Green Computing  
**Alignment:** UN Sustainable Development Goal 13 — Climate Action

---

## Features

| Feature | Description |
|---------|-------------|
| **Complexity Detection** | Automatically detects Big-O time complexity (O(1) to O(n!)) via AST analysis |
| **Carbon Estimation** | Converts CPU time to energy (Wh) and then to CO2 emissions (gCO2) |
| **Function-Level Analysis** | Breaks down emissions per function/method |
| **Hotspot Detection** | Identifies the single worst-performing function in a file |
| **Optimization Suggestions** | Recommends concrete improvements with estimated % savings |
| **Environmental Equivalents** | Translates emissions to driving distance, tree-days, phone charges |
| **Project Analysis** | Scans an entire directory and reports on all Java files |
| **Demo / Algorithm Comparison** | Side-by-side carbon ranking of 7 sample algorithms |
| **Export Report** | Saves a plain-text report to disk after any analysis |
| **Interactive Menu** | Full terminal UI with ANSI colors and a progress spinner |

---

## How It Works

```
Source Code (.java)
       |
       v
  Java Parser (AST)
       |
       v
  Complexity Detector
  - Loop nesting depth
  - Recursion detection
  - Divide-and-conquer patterns
  - Known algorithm name matching
       |
       v
  Carbon Calculator
  1. Complexity -> estimated operations (for n = 1000)
  2. Operations -> CPU cycles
  3. CPU cycles -> execution time (seconds)
  4. Execution time * CPU power (65W) -> energy (Wh)
  5. Energy * carbon intensity (475 gCO2/kWh) -> CO2 (grams)
       |
       v
  Optimization Engine
  - Suggests algorithm replacements
  - Recommends data structure changes
  - Identifies caching opportunities
       |
       v
  Report Formatter
  - ANSI-colored terminal output
  - Ranked function table
  - Suggestion cards
  - Environmental impact summary
```

---

## Getting Started

### Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK | 17 or higher |
| Apache Maven | 3.6+ |

### Build

```bash
# Clone or download the project
cd Eco-Code

# Build the fat JAR (includes all dependencies)
mvn clean package -q

# The runnable JAR will be at:
# target/ecocode-analyzer.jar
```

> **Note:** If Maven is not on your PATH, you can compile manually using `javac` and update the existing JAR with `jar uf`. See [Manual Build](#manual-build) below.

#### Manual Build (no Maven required)

```powershell
$javac = "C:\Program Files\Java\jdk-25.0.2\bin\javac.exe"
$jar   = "C:\Program Files\Java\jdk-25.0.2\bin\jar.exe"

& $javac --release 17 -cp "target\ecocode-analyzer.jar" -d "target\classes" `
    "src\main\java\com\ecocode\Main.java" `
    "src\main\java\com\ecocode\ui\ReportFormatter.java"

& $jar uf "target\ecocode-analyzer.jar" -C "target\classes" "com/ecocode/Main.class"
```

### Run

```bash
# Interactive menu (recommended)
java -jar target/ecocode-analyzer.jar

# Analyze a single file directly
java -jar target/ecocode-analyzer.jar path/to/YourFile.java
```

> **Windows Terminal users:** The tool auto-configures UTF-8 output. Run directly from Windows Terminal for best results.

---

## Usage

### Interactive Mode

Launch without arguments to enter the full interactive menu:

```
  ==========================================================
         EcoCode Analyzer  v1.0.0
         Carbon Footprint Analysis for Source Code
         Making Software Environmentally Conscious
  ==========================================================

  

  ----------------------------------------------------------
  MAIN MENU
  ----------------------------------------------------------
  [1]  Analyze Single File         scan one source file
  [2]  Analyze Project Directory   scan all files in a folder
  [3]  Demo -- Compare Algorithms  side-by-side comparison
  [4]  About                       project info & methodology
  [5]  Help                        usage & supported languages
  [6]  Exit                        quit EcoCode Analyzer
  ----------------------------------------------------------
  >
```

**Option 1 — Analyze Single File**  
Enter the path to any `.java` file. The tool parses it, detects complexity for each method, and prints a full report with a carbon gauge bar, function table, suggestion cards, and environmental equivalents. You are then prompted to optionally save the report to a `.txt` file.

**Option 2 — Analyze Project Directory**  
Enter the path to a project root. The tool recursively scans all `.java` files (skipping `target/`, `build/`, `.git/`, and similar directories), produces a report per file, and prints a summary of the whole project's total emissions.

**Option 3 — Demo Mode**  
Automatically analyzes all 7 bundled sample algorithms and prints a ranked comparison table (see [Demo Mode](#demo-mode) section).

### Command-Line Mode

```bash
# Analyze a single file — no menu, prints report and exits
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/BubbleSort.java

# Show help
java -jar target/ecocode-analyzer.jar --help

# Show version
java -jar target/ecocode-analyzer.jar --version
```

### Demo Mode

Select option `3` in the menu to see a live comparison of all sample algorithms:

```
  ==========================================================
  ALGORITHM COMPARISON -- Carbon Footprint Ranking
  ==========================================================

  Rank  Algorithm              Complexity        Carbon(gCO2)    Rating
  ------------------------------------------------------------------------------
  1.    HashMapLookup          O(1)              0.0000          Excellent  [5/5]
  2.    BinarySearch           O(log n)          0.0000          Excellent  [5/5]
  3.    LinearSearch           O(n)              0.0000          Excellent  [5/5]
  4.    QuickSort              O(n log n)        0.0000          Excellent  [5/5]
  5.    BubbleSort             O(n^2)            0.0001          Excellent  [5/5]
  6.    Fibonacci              O(2^n)            0.0312          Excellent  [5/5]
  7.    MatrixMultiply         O(n^3)            0.1226          Excellent  [5/5]

  ----------------------------------------------------------
  -- VERDICT --
  ----------------------------------------------------------
  [+] Best algorithm:  HashMapLookup  (O(1))  -- least carbon
  [!] Worst algorithm: MatrixMultiply (O(n^3)) -- most carbon

  [$] Potential savings: 0.1226 gCO2 if you replaced MatrixMultiply with HashMapLookup
```

---

## Sample Output

Analyzing `BubbleSort.java`:

```
  ==========================================================
       EcoCode Analyzer v1.0.0  --  Carbon Analysis
  ==========================================================

  Analyzing: BubbleSort.java

  [/] Analyzing code...  [OK] Analysis complete

  ==========================================================
       EcoCode Analyzer -- Carbon Emission Report
  ==========================================================

  File:               BubbleSort.java
  Lines of Code:      50

  -- CARBON FOOTPRINT --

  Carbon Emissions:   0.0001 gCO2
  Footprint Level:    [#.........]  Excellent
  Estimated CPU Time: 14.30 ms
  Energy Consumption: 0.000258 Wh
  Rating:             Excellent  [5/5]

  -- ENVIRONMENTAL EQUIVALENTS --

  Driving:              0.0 meters
  Trees needed:         0.0000 tree-days
  Smartphone charges:   0.0 charges
     Minimal environmental impact - Great job!

  -- FUNCTION-LEVEL ANALYSIS --

  Function                   Complexity     Carbon(gCO2)   Time(ms)
  --------------------------------------------------------------------
  bubbleSort                 O(n^2)         0.0001         14.29
  main                       O(1)           0.0000         0.00
  printArray                 O(n)           0.0000         0.01

  [!] Hotspot: bubbleSort -- has the highest carbon emissions

  -- OPTIMIZATION SUGGESTIONS --

  +----------------------------------------------------------+
  | [X] Suggestion #1 -- CRITICAL PRIORITY
  +----------------------------------------------------------+
  |   Function:   bubbleSort
  |   Type:       Algorithm Replacement
  |   Inefficient sorting algorithm detected. Use QuickSort, MergeSort, or Arrays.sort().
  |   [X] Current:   O(n^2)
  |   [+] Suggested: O(n log n)
  |   [$] Saves: 0.0001 gCO2  (99.0% reduction)
  +----------------------------------------------------------+

  ----------------------------------------------------------
  EcoCode Analyzer v1.0.0  |  Making Code Greener
```

---

## Algorithm Comparison

The tool ships with 7 sample files covering the full Big-O complexity spectrum:

| File | Algorithm | Time Complexity | Notes |
|------|-----------|-----------------|-------|
| `HashMapLookup.java` | HashMap get/put | O(1) | Best-case constant time |
| `BinarySearch.java` | Binary Search | O(log n) | Divide-and-conquer search |
| `LinearSearch.java` | Linear Search | O(n) | Sequential scan |
| `QuickSort.java` | Quick Sort | O(n log n) | Efficient in-place sort |
| `BubbleSort.java` | Bubble Sort | O(n²) | Classic inefficient sort |
| `Fibonacci.java` | Recursive Fibonacci | O(2^n) | Exponential — no memoization |
| `MatrixMultiply.java` | Matrix Multiplication | O(n³) | Cubic nested loops |

---

## Project Structure

```
Eco-Code/
+-- src/
|   +-- main/
|   |   +-- java/com/ecocode/
|   |   |   +-- Main.java                    # Entry point, interactive menu, demo mode
|   |   |   +-- core/
|   |   |   |   +-- CodeAnalyzer.java        # Orchestrates analysis pipeline
|   |   |   |   +-- ComplexityDetector.java  # AST-based Big-O detection
|   |   |   |   +-- CarbonCalculator.java    # IEEE carbon emission model
|   |   |   |   +-- OptimizationEngine.java  # Generates improvement suggestions
|   |   |   +-- models/
|   |   |   |   +-- Complexity.java          # Big-O enum (O(1) ... O(n!))
|   |   |   |   +-- ComplexityResult.java    # Per-method analysis result
|   |   |   |   +-- CarbonReport.java        # Full file report + hotspot detection
|   |   |   |   +-- FunctionAnalysis.java    # Per-function metrics
|   |   |   |   +-- OptimizationSuggestion.java
|   |   |   |   +-- EnvironmentalMetrics.java
|   |   |   |   +-- CodeFile.java
|   |   |   +-- ui/
|   |   |       +-- ReportFormatter.java     # ANSI terminal output formatter
|   |   +-- resources/
|   |       +-- sample-code/                 # 7 sample algorithms for demo/testing
|   |           +-- BubbleSort.java
|   |           +-- BinarySearch.java
|   |           +-- Fibonacci.java
|   |           +-- QuickSort.java
|   |           +-- LinearSearch.java
|   |           +-- MatrixMultiply.java
|   |           +-- HashMapLookup.java
|   +-- test/java/                           # Unit tests
+-- target/
|   +-- ecocode-analyzer.jar                 # Runnable fat JAR (all deps included)
+-- pom.xml                                  # Maven build configuration
+-- statement.md                             # Problem statement
+-- README.md                                # This file
```

---

## Technology Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Language | Java 17 (compiled), Java 25 (runtime) | Core implementation |
| Build Tool | Apache Maven 3.x | Dependency management, packaging |
| AST Parser | JavaParser 3.25.4 | Source code parsing and traversal |
| ANSI Colors | Jansi 2.4.0 | Terminal color output on all platforms |
| Packaging | Maven Shade Plugin | Fat JAR with all dependencies bundled |

---

## Carbon Calculation Model

The emission calculation follows five steps using the IEEE software energy estimation model:

**Step 1 — Estimate Operations**

```
operations = complexity.estimate(n=1000)
  e.g.  O(n)   -> 1,000 operations
        O(n^2) -> 1,000,000 operations
```

**Step 2 — CPU Cycles**

```
cycles = operations * BASE_INSTRUCTIONS_PER_OPERATION
       / INSTRUCTIONS_PER_CYCLE
```

**Step 3 — Execution Time**

```
time_seconds = cycles / (CPU_FREQUENCY_GHz * 10^9)
```

**Step 4 — Energy**

```
energy_Wh = CPU_POWER_W * time_seconds / 3600
  CPU_POWER_W = 65W  (average desktop processor)
```

**Step 5 — Carbon Emissions**

```
carbon_gCO2 = energy_Wh * CARBON_INTENSITY / 1000
  CARBON_INTENSITY = 475 gCO2/kWh  (global average grid)
```

**Complexity Ordering (by severity):**

```
O(1) < O(log n) < O(n) < O(n log n) < O(n^2) < O(n^3) < O(2^n) < O(n!)
```

---

## Testing

### Run Unit Tests

```bash
mvn test
```

### Test with Sample Files

```bash
# Efficient algorithm (O(1))
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/HashMapLookup.java

# Quadratic algorithm (O(n^2)) -- should show suggestion to optimize
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/BubbleSort.java

# Exponential algorithm (O(2^n)) -- most carbon
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/Fibonacci.java

# Logarithmic algorithm (O(log n))
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/BinarySearch.java
```

### Expected Results

| Sample File | Detected Complexity | Hotspot | Suggestion Expected |
|-------------|--------------------|---------|--------------------|
| `HashMapLookup.java` | O(1) | None | None (already optimal) |
| `BinarySearch.java` | O(log n) | None | None (already optimal) |
| `LinearSearch.java` | O(n) | None | None |
| `QuickSort.java` | O(n log n) | None | None |
| `BubbleSort.java` | O(n²) | bubbleSort | Use QuickSort / Arrays.sort() |
| `Fibonacci.java` | O(2^n) | fibonacci | Use dynamic programming |
| `MatrixMultiply.java` | O(n³) | multiply | Algorithm optimization |


