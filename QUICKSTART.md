# Quick Start Guide - EcoCode Analyzer

## Option 1: Using Maven (Recommended)

### Prerequisites
1. Install Java 17+: https://www.oracle.com/java/technologies/downloads/
2. Install Maven: https://maven.apache.org/download.cgi

### Build and Run
```bash
# Navigate to project directory
cd VITyarthi_Java

# Build the project
mvn clean package

# Run in interactive mode
java -jar target/ecocode-analyzer.jar --interactive

# Analyze a sample file
java -jar target/ecocode-analyzer.jar src/main/resources/sample-code/BubbleSort.java
```

---

## Option 2: Manual Compilation (If Maven not available)

### Step 1: Download Dependencies
You'll need to manually download these JARs:
- javaparser-core-3.25.4.jar
- fusesource-jansi-2.4.0.jar
- gson-2.10.1.jar

Place them in a `lib/` folder in the project root.

### Step 2: Compile
```bash
javac -cp "lib/*" -d bin src/main/java/com/ecocode/**/*.java
```

### Step 3: Run
```bash
java -cp "bin;lib/*" com.ecocode.Main --interactive
```

---

## What to Expect

### First Run - Interactive Mode

When you run the application, you'll see:

```
╔═══════════════════════════════════════════════════════════════════════════════╗
║                                                                               ║
║                        🌱 ECOCODE ANALYZER 🌱                                 ║
║                                                                               ║
║              Carbon Footprint Analysis for Source Code                        ║
║              Making Software Environmentally Conscious                        ║
║                                                                               ║
║                           Version 1.0.0                                       ║
║                                                                               ║
╚═══════════════════════════════════════════════════════════════════════════════╝

┌─ MAIN MENU ─────────────────────────────────────────────────────────────┐
│                                                                           │
│  1. Analyze Single File                                                  │
│  2. Analyze Project Directory                                            │
│  3. About                                                                 │
│  4. Help                                                                  │
│  5. Exit                                                                  │
│                                                                           │
└───────────────────────────────────────────────────────────────────────────┘

Your choice: 
```

### Sample Analysis

Try analyzing the provided sample files:

#### Example 1: BubbleSort.java (Inefficient - O(n²))
```
File: BubbleSort.java
Total Carbon Emissions: ~12.3456 gCO₂ 🚨
Rating: Poor ⭐⭐

Optimization Suggestion:
  🚨 High Priority
  Replace Bubble Sort with QuickSort or MergeSort
  Current: O(n²) → Suggested: O(n log n)
  Potential Savings: 11.2 gCO₂ (91% reduction)
```

#### Example 2: BinarySearch.java (Efficient - O(log n))
```
File: BinarySearch.java
Total Carbon Emissions: ~0.0811 gCO₂ 🌱
Rating: Excellent ⭐⭐⭐⭐⭐

✓ No optimization suggestions - code is already efficient!
```

#### Example 3: Fibonacci.java (Exponential - O(2^n))
```
File: Fibonacci.java
Total Carbon Emissions: ~245.67 gCO₂ 🚨
Rating: Very Poor ⭐

Optimization Suggestion:
  🚨 CRITICAL Priority
  Exponential recursive Fibonacci detected
  Current: O(2^n) → Suggested: O(n) with Dynamic Programming
  Potential Savings: 244.8 gCO₂ (99.6% reduction)
```

---

## Project Structure

```
VITyarthi_Java/
├── src/main/java/com/ecocode/
│   ├── Main.java                   # Entry point
│   ├── core/                       # Analysis engines
│   ├── models/                     # Data models
│   └── ui/                         # User interface
├── src/main/resources/sample-code/ # Test files
├── README.md                       # Full documentation
├── statement.md                    # Problem statement
└── pom.xml                         # Maven config
```

---

## Key Features Demonstrated

1. ✅ **Complexity Detection**: Automatically detects O(1), O(log n), O(n), O(n²), O(2^n)
2. ✅ **Carbon Calculation**: Converts complexity to CO₂ emissions
3. ✅ **Environmental Metrics**: Shows driving distance, tree absorption equivalents
4. ✅ **Optimization Suggestions**: Actionable recommendations with savings
5. ✅ **Priority Ranking**: LOW, MEDIUM, HIGH, CRITICAL priorities

---

## Troubleshooting

### Issue: Maven not recognized
**Solution**: Install Maven from https://maven.apache.org/download.cgi

### Issue: Java version error
**Solution**: Ensure Java 17+ is installed (`java -version`)

### Issue: Dependencies not found
**Solution**: Run `mvn clean install` to download dependencies

---

## Next Steps After Building

1. **Test the samples**: Run analyzer on all 3 sample files
2. **Analyze your own code**: Point it to your Java files
3. **Review suggestions**: See what optimizations are recommended
4. **Track improvements**: Re-analyze after applying suggestions

---

## For Demonstration/Presentation

### Best Files to Demo:
1. **BubbleSort.java** - Shows inefficiency detection
2. **Fibonacci.java** - Shows critical optimization needs
3. **BinarySearch.java** - Shows efficient code recognition

### Talking Points:
- "This O(n²) algorithm emits X grams of CO₂"
- "That's equivalent to driving Y meters in a car"
- "By optimizing to O(n log n), we save Z% emissions"
- "The tool automatically detected this and suggested improvements"

---

## Need Help?

Run with `--help` flag for complete usage information:
```bash
java -jar ecocode-analyzer.jar --help
```

---

**Remember**: Every optimization reduces both carbon emissions AND computational costs! 🌱
