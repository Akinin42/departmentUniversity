package org.university.exceptions;

public class UploadS3Exception extends RuntimeException {
    
    public UploadS3Exception(String messages) {
        super(messages);
    }
}
