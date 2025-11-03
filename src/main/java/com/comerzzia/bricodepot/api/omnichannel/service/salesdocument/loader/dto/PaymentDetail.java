package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto;

import java.math.BigDecimal;

public class PaymentDetail {

    private final String code;
    private final String description;
    private final BigDecimal amount;

    public PaymentDetail(String code, String description, BigDecimal amount) {
        this.code = code;
        this.description = description;
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
