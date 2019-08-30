package com.vastserver.classes.vastErrorHandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class VastErrorResponse {
    private HttpStatus status;
    private String primaryMessage;
    private String secondaryMessage;
    private HttpHeaders headers;

    public VastErrorResponse(HttpStatus status,
                             String primaryMessage,
                             String secondaryMessage,
                             HttpHeaders headers) {
        this.status = status;
        this.primaryMessage = primaryMessage;
        this.secondaryMessage = secondaryMessage;
        this.headers = headers;
    }
    public ResponseEntity<Object> getErrorResponse() {
        ApiError apiError = new ApiError(this.status,
                this.primaryMessage, this.secondaryMessage);
        return new ResponseEntity<>(
                apiError, this.headers, this.status);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getPrimaryMessage() {
        return primaryMessage;
    }

    public void setPrimaryMessage(String primaryMessage) {
        this.primaryMessage = primaryMessage;
    }

    public String getSecondaryMessage() {
        return secondaryMessage;
    }

    public void setSecondaryMessage(String secondaryMessage) {
        this.secondaryMessage = secondaryMessage;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "VastErrorResponse{" +
                "status=" + status +
                ", primaryMessage='" + primaryMessage + '\'' +
                ", secondaryMessage='" + secondaryMessage + '\'' +
                ", headers=" + headers +
                '}';
    }
}
