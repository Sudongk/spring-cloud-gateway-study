package com.example.gateway.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class TokenInfo {

    private UUID id;
    private String name;
    private String number;
    private String role;

}
