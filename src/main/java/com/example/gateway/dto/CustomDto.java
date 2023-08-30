package com.example.gateway.dto;

public class CustomDto {

    private Boolean logging;

    public CustomDto() {
    }

    public CustomDto(Boolean logging) {
        this.logging = logging;
    }

    public void setLogging(Boolean logging) {
        this.logging = logging;
    }

    public Boolean getLogging() {
        return logging;
    }
}
