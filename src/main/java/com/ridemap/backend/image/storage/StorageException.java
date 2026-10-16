package com.ridemap.backend.image.storage;

public class StorageException extends RuntimeException {
    public StorageException(String message, Throwable cause){
        super(message, cause);
    }
}
