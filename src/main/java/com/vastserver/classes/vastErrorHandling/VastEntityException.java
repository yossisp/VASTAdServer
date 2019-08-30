package com.vastserver.classes.vastErrorHandling;

public class VastEntityException extends Exception {
    private String errorMsg;

    public VastEntityException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "VastEntityException: " + this.errorMsg;
    }
}
