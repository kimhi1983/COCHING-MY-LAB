package com.erns.es.doc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import com.erns.es.doc.type.SupportDocType;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = {"sourceInputStream", "destinationOutputStream"})
public class DocToPdfConfig {
	private final SupportDocType sourceFormat;
	
    private final InputStream sourceInputStream;
    private final String sourcePath;
    private final Path sourceFilePath;
    
    private final OutputStream destinationOutputStream;    
    private final String destinationPath;    
    private final Path destinationFilePath;
    
    private final String watermarkText;

    @Builder
    private DocToPdfConfig(
    	SupportDocType sourceFormat,
        
    	InputStream sourceInputStream,
    	String sourcePath,
    	Path sourceFilePath, 
        
    	OutputStream destinationOutputStream,
        String destinationPath,
        Path destinationFilePath,
         
        String watermarkText) {
    	
        this.sourceFormat = sourceFormat;
        
		this.sourceInputStream = sourceInputStream;
        this.sourcePath = sourcePath;
        this.sourceFilePath = sourceFilePath;        
        this.destinationOutputStream = destinationOutputStream;        
        this.destinationPath = destinationPath;
        
        this.destinationFilePath = destinationFilePath;
        this.watermarkText = watermarkText;
    }
    
    //필수값 검사
    public void validate() {
    	if ((sourceInputStream == null && sourcePath == null && sourceFilePath == null) ||
            (destinationOutputStream == null && destinationPath == null && destinationFilePath == null)) {
            throw new IllegalStateException("Source and Destination must both be set with at least one valid input/output type.");
        }
    	
    	if(sourceFormat == SupportDocType.HWP || sourceFormat == SupportDocType.HWPX) {
    		if (sourceInputStream != null || destinationOutputStream != null) {
	            throw new IllegalStateException("HWP, HWPX. Source and Destination cannot use stream[input/output] type.");
	        }
    	}
    	
    	if(sourceFormat == null) {
    		throw new IllegalStateException("Unsupported source format.");
    	}
    }
    
    public String getSourcePath() {
    	if (sourcePath != null) {
            return sourcePath;
        }
    	
    	if (sourceFilePath != null) {
            return sourceFilePath.toString();
        }
    	
		return sourcePath;
	}

	public Path getSourceFilePath() {
		if (sourceFilePath != null) {
            return sourceFilePath;
        }
		
		if (sourcePath != null) {
            return Paths.get(sourcePath);
        }
    	
		return sourceFilePath;
	}

	public String getDestinationPath() {
		if (destinationPath != null) {
            return destinationPath;
        }
    	
    	if (destinationFilePath != null) {
            return destinationFilePath.toString();
        }
    	
		return destinationPath;
	}

	public Path getDestinationFilePath() {
		if (destinationFilePath != null) {
            return destinationFilePath;
        }
		
		if (destinationPath != null) {
            return Paths.get(destinationPath);
        }
		
		return destinationFilePath;
	}
    
    
    
    // Method to get source InputStream
    public InputStream getSourceInputStream() throws IOException {
        if (sourceInputStream != null) {
            return sourceInputStream;
        }

        // Fallback to sourcePath if present
        if (sourcePath != null) {
            return new FileInputStream(sourcePath);
        }

        // Fallback to sourceFilePath if present
        if (sourceFilePath != null) {
            return Files.newInputStream(sourceFilePath);
        }

        throw new IllegalStateException("No valid source input available");
    }
    
    // Method to get destination OutputStream
    public OutputStream getDestinationOutputStream() throws IOException {
        if (destinationOutputStream != null) {
            return destinationOutputStream;
        }

        if (destinationPath != null) {
            return new FileOutputStream(destinationPath);
        }

        if (destinationFilePath != null) {
            return Files.newOutputStream(destinationFilePath);
        }

        throw new IllegalStateException("No valid destination output available");
    }
    
	public static class DocToPdfConfigBuilder {
        public DocToPdfConfigBuilder sourcePath(String sourcePath) {
            this.sourcePath = sourcePath;
            this.sourceInputStream = null;
            this.sourceFilePath = null; // Ensure mutual exclusivity
            
            String fileExtension = extractFileExtension(sourcePath).toLowerCase(Locale.ROOT);
            this.sourceFormat = SupportDocType.findByFileExtension(fileExtension);
            return this;
        }

        public DocToPdfConfigBuilder sourceInputStream(InputStream sourceInputStream) {
            this.sourceInputStream = sourceInputStream;
            this.sourcePath = null;
            this.sourceFilePath = null; // Ensure mutual exclusivity
            return this;
        }

        public DocToPdfConfigBuilder sourceFilePath(Path sourceFilePath) {
            this.sourceFilePath = sourceFilePath;
            this.sourcePath = null;
            this.sourceInputStream = null; // Ensure mutual exclusivity
            
            String fileExtension = extractFileExtension(sourceFilePath.toString()).toLowerCase(Locale.ROOT);
            this.sourceFormat = SupportDocType.findByFileExtension(fileExtension);            
            return this;
        }

        public DocToPdfConfigBuilder destinationPath(String destinationPath) {
            this.destinationPath = destinationPath;
            this.destinationOutputStream = null;
            this.destinationFilePath = null; // Ensure mutual exclusivity
            return this;
        }

        public DocToPdfConfigBuilder destinationOutputStream(OutputStream destinationOutputStream) {
            this.destinationOutputStream = destinationOutputStream;
            this.destinationPath = null;
            this.destinationFilePath = null; // Ensure mutual exclusivity
            return this;
        }

        public DocToPdfConfigBuilder destinationFilePath(Path destinationFilePath) {
            this.destinationFilePath = destinationFilePath;
            this.destinationPath = null;
            this.destinationOutputStream = null; // Ensure mutual exclusivity
            return this;
        }
        
        // Utility method to extract file extension
        private String extractFileExtension(String filePath) {
            if (filePath != null && filePath.contains(".")) {
                return filePath.substring(filePath.lastIndexOf(".") + 1);
            }
            return null; // No extension found
        }
    }
}
