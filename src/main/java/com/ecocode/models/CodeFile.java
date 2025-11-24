package com.ecocode.models;

/**
 * Represents a source code file to be analyzed
 */
public class CodeFile {
    private String fileName;
    private String filePath;
    private String content;
    private ProgrammingLanguage language;
    private int linesOfCode;
    private long fileSize; // in bytes

    public enum ProgrammingLanguage {
        JAVA(".java", "Java"),
        PYTHON(".py", "Python"),
        JAVASCRIPT(".js", "JavaScript"),
        TYPESCRIPT(".ts", "TypeScript"),
        CPP(".cpp", "C++"),
        C(".c", "C"),
        CSHARP(".cs", "C#"),
        UNKNOWN("", "Unknown");

        private final String extension;
        private final String displayName;

        ProgrammingLanguage(String extension, String displayName) {
            this.extension = extension;
            this.displayName = displayName;
        }

        public String getExtension() {
            return extension;
        }

        public String getDisplayName() {
            return displayName;
        }

        /**
         * Detect language from file extension
         */
        public static ProgrammingLanguage fromFileName(String fileName) {
            if (fileName == null) return UNKNOWN;
            
            String lowerName = fileName.toLowerCase();
            for (ProgrammingLanguage lang : values()) {
                if (lowerName.endsWith(lang.extension)) {
                    return lang;
                }
            }
            return UNKNOWN;
        }
    }

    public CodeFile() {
    }

    public CodeFile(String fileName, String filePath, String content) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.content = content;
        this.language = ProgrammingLanguage.fromFileName(fileName);
        this.linesOfCode = content != null ? content.split("\n").length : 0;
        this.fileSize = content != null ? content.length() : 0;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.language = ProgrammingLanguage.fromFileName(fileName);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.linesOfCode = content != null ? content.split("\n").length : 0;
        this.fileSize = content != null ? content.length() : 0;
    }

    public ProgrammingLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ProgrammingLanguage language) {
        this.language = language;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public long getFileSize() {
        return fileSize;
    }

    public boolean isSupported() {
        return language != ProgrammingLanguage.UNKNOWN;
    }

    @Override
    public String toString() {
        return String.format("CodeFile{name='%s', language=%s, LOC=%d}",
                fileName, language.getDisplayName(), linesOfCode);
    }
}
