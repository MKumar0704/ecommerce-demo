package com.ecom.productservice.exception;

import java.util.function.Supplier;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
