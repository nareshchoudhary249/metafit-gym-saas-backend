package com.metafit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when file upload validation fails
 * Examples: Invalid file type, file too large, corrupted file
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileException extends RuntimeException {

    private final String fileName;
    private final String fileType;
    private final long fileSize;

    public InvalidFileException(String message) {
        super(message);
        this.fileName = null;
        this.fileType = null;
        this.fileSize = 0;
    }

    public InvalidFileException(String message, String fileName, String fileType, long fileSize) {
        super(message);
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public long getFileSize() {
        return fileSize;
    }
}
