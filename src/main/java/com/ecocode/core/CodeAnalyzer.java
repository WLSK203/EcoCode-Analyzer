package com.ecocode.core;

import com.ecocode.models.*;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * Main orchestrator for code analysis
 * Coordinates parsing, complexity detection, and carbon calculation
 */
public class CodeAnalyzer {
    
    private final ComplexityDetector complexityDetector;
    private final CarbonCalculator carbonCalculator;
    private final OptimizationEngine optimizationEngine;
    private final JavaParser javaParser;
    
    public CodeAnalyzer() {
        this.complexityDetector = new ComplexityDetector();
        this.carbonCalculator = new CarbonCalculator();
        this.optimizationEngine = new OptimizationEngine(carbonCalculator);
        this.javaParser = new JavaParser();
    }
    
    /**
     * Analyze a single code file
     */
    public CarbonReport analyzeFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        
        String content = Files.readString(file.toPath());
        CodeFile codeFile = new CodeFile(file.getName(), filePath, content);
        
        return analyzeCodeFile(codeFile);
    }
    
    /**
     * Analyze a CodeFile object
     */
    public CarbonReport analyzeCodeFile(CodeFile codeFile) {
        CarbonReport report = new CarbonReport(codeFile.getFileName(), codeFile.getFilePath());
        report.setTotalLinesOfCode(codeFile.getLinesOfCode());
        
        // Check if language is supported
        if (!codeFile.isSupported()) {
            throw new UnsupportedOperationException(
                "Unsupported language: " + codeFile.getLanguage().getDisplayName() + 
                ". Currently supported: Java, Python, JavaScript, C/C++"
            );
        }
        
        // Route to appropriate parser based on language
        switch (codeFile.getLanguage()) {
            case JAVA:
                analyzeJavaCode(codeFile, report);
                break;
            case PYTHON:
                analyzePythonCode(codeFile, report);
                break;
            case JAVASCRIPT:
            case TYPESCRIPT:
                analyzeJavaScriptCode(codeFile, report);
                break;
            case CPP:
            case C:
                analyzeCppCode(codeFile, report);
                break;
            default:
                throw new UnsupportedOperationException("Parser not implemented for: " + codeFile.getLanguage());
        }
        
        // Finalize report
        report.calculateEnvironmentalMetrics();
        report.identifyWorstFunction();
        
        return report;
    }
    
    /**
     * Analyze Java source code
     */
    private void analyzeJavaCode(CodeFile codeFile, CarbonReport report) {
        try {
            ParseResult<CompilationUnit> parseResult = javaParser.parse(codeFile.getContent());
            
            if (!parseResult.isSuccessful()) {
                throw new RuntimeException("Failed to parse Java file: " + 
                    parseResult.getProblems().toString());
            }
            
            Optional<CompilationUnit> cu = parseResult.getResult();
            if (cu.isEmpty()) {
                return;
            }
            
            // Find all methods and analyze them
            List<MethodDeclaration> methods = cu.get().findAll(MethodDeclaration.class);
            
            for (MethodDeclaration method : methods) {
                analyzeJavaMethod(method, report);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing Java code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Analyze a single Java method
     */
    private void analyzeJavaMethod(MethodDeclaration method, CarbonReport report) {
        // Detect complexity
        ComplexityResult complexityResult = complexityDetector.analyzeMethod(method);
        
        // Create function analysis
        FunctionAnalysis analysis = new FunctionAnalysis(
            method.getNameAsString(), 
            complexityResult
        );
        
        // Calculate lines of code for this method
        int methodLOC = method.getEnd().map(pos -> pos.line).orElse(0) - 
                        method.getBegin().map(pos -> pos.line).orElse(0) + 1;
        analysis.setLinesOfCode(methodLOC);
        
        // Calculate carbon emissions
        carbonCalculator.calculateFunctionEmissions(analysis);
        
        // Add to report
        report.addFunctionAnalysis(analysis);
        
        // Generate optimization suggestions
        List<OptimizationSuggestion> suggestions = optimizationEngine.generateSuggestions(
            analysis, method
        );
        for (OptimizationSuggestion suggestion : suggestions) {
            report.addSuggestion(suggestion);
        }
    }
    
    /**
     * Analyze Python source code (placeholder - basic implementation)
     */
    private void analyzePythonCode(CodeFile codeFile, CarbonReport report) {
        // TODO: Implement Python parser using ANTLR
        // For now, create a basic analysis
        FunctionAnalysis basicAnalysis = createBasicAnalysis(
            "main", 
            Complexity.O_N, 
            codeFile.getLinesOfCode()
        );
        carbonCalculator.calculateFunctionEmissions(basicAnalysis);
        report.addFunctionAnalysis(basicAnalysis);
    }
    
    /**
     * Analyze JavaScript/TypeScript code (placeholder)
     */
    private void analyzeJavaScriptCode(CodeFile codeFile, CarbonReport report) {
        // TODO: Implement JavaScript parser
        FunctionAnalysis basicAnalysis = createBasicAnalysis(
            "main", 
            Complexity.O_N, 
            codeFile.getLinesOfCode()
        );
        carbonCalculator.calculateFunctionEmissions(basicAnalysis);
        report.addFunctionAnalysis(basicAnalysis);
    }
    
    /**
     * Analyze C/C++ code (placeholder)
     */
    private void analyzeCppCode(CodeFile codeFile, CarbonReport report) {
        // TODO: Implement C/C++ parser using ANTLR
        FunctionAnalysis basicAnalysis = createBasicAnalysis(
            "main", 
            Complexity.O_N, 
            codeFile.getLinesOfCode()
        );
        carbonCalculator.calculateFunctionEmissions(basicAnalysis);
        report.addFunctionAnalysis(basicAnalysis);
    }
    
    /**
     * Create a basic analysis for unsupported languages
     */
    private FunctionAnalysis createBasicAnalysis(String name, Complexity complexity, int loc) {
        ComplexityResult result = new ComplexityResult(name, complexity, Complexity.O_1);
        result.setConfidenceScore(0.5);
        result.setDetectionMethod("Basic estimation (language parser not fully implemented)");
        
        FunctionAnalysis analysis = new FunctionAnalysis(name, result);
        analysis.setLinesOfCode(loc);
        
        return analysis;
    }
    
    /**
     * Analyze an entire project directory
     */
    public ProjectReport analyzeProject(String projectPath) throws IOException {
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            throw new IOException("Project directory not found: " + projectPath);
        }
        
        ProjectReport projectReport = new ProjectReport(projectPath);
        analyzeDirectory(projectDir, projectReport);
        
        return projectReport;
    }
    
    /**
     * Recursively analyze directory
     */
    private void analyzeDirectory(File directory, ProjectReport projectReport) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                // Skip common directories
                if (file.getName().equals("target") || 
                    file.getName().equals("node_modules") ||
                    file.getName().equals(".git")) {
                    continue;
                }
                analyzeDirectory(file, projectReport);
            } else {
                // Check if it's a supported source file
                CodeFile.ProgrammingLanguage lang = CodeFile.ProgrammingLanguage.fromFileName(file.getName());
                if (lang != CodeFile.ProgrammingLanguage.UNKNOWN) {
                    try {
                        CarbonReport fileReport = analyzeFile(file.getAbsolutePath());
                        projectReport.addFileReport(fileReport);
                    } catch (Exception e) {
                        System.err.println ("Warning: Failed to analyze " + file.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * Simple inner class for project-level reports
     */
    public static class ProjectReport {
        private final String projectPath;
        private final java.util.List<CarbonReport> fileReports;
        
        public ProjectReport(String projectPath) {
            this.projectPath = projectPath;
            this.fileReports = new java.util.ArrayList<>();
        }
        
        public void addFileReport(CarbonReport report) {
            fileReports.add(report);
        }
        
        public List<CarbonReport> getFileReports() {
            return fileReports;
        }
        
        public double getTotalCarbonEmissions() {
            return fileReports.stream()
                .mapToDouble(CarbonReport::getTotalCarbonEmissions)
                .sum();
        }
        
        public int getTotalLinesOfCode() {
            return fileReports.stream()
                .mapToInt(CarbonReport::getTotalLinesOfCode)
                .sum();
        }
        
        public String getProjectPath() {
            return projectPath;
        }
    }
}
