package com.eCommerceWebsite.eCommerceWeb.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String fieldName;
    String field;
    long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message, String resourceName, String fieldName, String field) {

        super(String.format("%s not found with %s:%s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException( String field, String resourceName , long fieldId) {

        super(String.format("%s not found with %s:%d", resourceName, field, fieldId));
        this.field = field;
        this.fieldId = fieldId;
        this.resourceName = resourceName;
    }
}
