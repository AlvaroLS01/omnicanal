package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Aggregated data required to feed the Jasper report. The structure mirrors the legacy
 * backoffice {@code TrabajoInformeBean} content so that all parameters expected by the
 * templates are available.
 */
public class LoadedSaleDocument {

    private final String activityUid;
    private final String documentUid;
    private final String ticketCode;
    private final String documentTypeDescription;
    private final OffsetDateTime documentDate;
    private final BigDecimal totalAmount;
    private final String customerName;
    private final List<PaymentDetail> payments;
    private final List<PromotionDetail> promotions;
    private final CardPaymentData cardPaymentData;
    private final boolean refund;
    private final boolean groupedLines;

    public LoadedSaleDocument(String activityUid,
                              String documentUid,
                              String ticketCode,
                              String documentTypeDescription,
                              OffsetDateTime documentDate,
                              BigDecimal totalAmount,
                              String customerName,
                              List<PaymentDetail> payments,
                              List<PromotionDetail> promotions,
                              CardPaymentData cardPaymentData,
                              boolean refund,
                              boolean groupedLines) {
        this.activityUid = activityUid;
        this.documentUid = documentUid;
        this.ticketCode = ticketCode;
        this.documentTypeDescription = documentTypeDescription;
        this.documentDate = documentDate;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.payments = payments == null ? Collections.emptyList() : payments;
        this.promotions = promotions == null ? Collections.emptyList() : promotions;
        this.cardPaymentData = cardPaymentData;
        this.refund = refund;
        this.groupedLines = groupedLines;
    }

    public String getActivityUid() {
        return activityUid;
    }

    public String getDocumentUid() {
        return documentUid;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public String getDocumentTypeDescription() {
        return documentTypeDescription;
    }

    public OffsetDateTime getDocumentDate() {
        return documentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<PaymentDetail> getPayments() {
        return payments;
    }

    public List<PromotionDetail> getPromotions() {
        return promotions;
    }

    public CardPaymentData getCardPaymentData() {
        return cardPaymentData;
    }

    public boolean isRefund() {
        return refund;
    }

    public boolean isGroupedLines() {
        return groupedLines;
    }
}
