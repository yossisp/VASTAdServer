package com.vastserver.classes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;

public class VastXmlResponse extends ResponseEntity<Object> {
    private @Nullable String responseBody;
    private @Nullable MultiValueMap<String, String> responseHeaders;
    private @NotNull HttpStatus responseStatus;

    public VastXmlResponse(@Nullable String responseBody,
                           @Nullable MultiValueMap<String, String> responseHeaders,
                           @NotNull HttpStatus responseStatus) {
        super(responseBody, responseHeaders, responseStatus);
    }

    @Nullable
    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(@Nullable String responseBody) {
        this.responseBody = responseBody;
    }

    @Nullable
    public MultiValueMap<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(@Nullable MultiValueMap<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
}
