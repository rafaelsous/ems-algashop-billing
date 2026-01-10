package com.rafaelsousa.algashop.billing.presentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiErrorResponse {
    private String title;
    private Integer status;
    private String type;
    private String instance;
}