package com.vastserver.classes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;

public class JSONResponse extends ResponseEntity<Object> {
    private @Nullable Object body;
    private @Nullable MultiValueMap<String, String> responseHeaders;
    private @NotNull HttpStatus status;

    public JSONResponse(@Nullable Object jsonBody,
                        @Nullable MultiValueMap<String, String> responseHeaders,
                        @NotNull HttpStatus status) {
        super(jsonBody, responseHeaders, status);
    }
}
