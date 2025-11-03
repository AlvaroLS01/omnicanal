package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report;

import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.CardPaymentData;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.LoadedSaleDocument;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.PaymentDetail;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.PromotionDetail;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SaleDocumentReportAssembler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaleDocumentReportAssembler.class);
    private static final String MAIN_TEMPLATE = "informes/ventas/facturas/facturaA4.jrxml";

    public SaleDocumentReport buildReport(LoadedSaleDocument document) {
        try {
            JasperReport report = compileReport();
            Map<String, Object> parameters = buildParameters(document);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource(1));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);
            String encoded = Base64.getEncoder().encodeToString(pdf);
            String fileName = buildFileName(document);
            return new SaleDocumentReport(fileName, "application/pdf", encoded);
        } catch (Exception ex) {
            LOGGER.error("Error generating invoice {}", document.getDocumentUid(), ex);
            throw new IllegalStateException("Unable to generate invoice", ex);
        }
    }

    private JasperReport compileReport() throws IOException, net.sf.jasperreports.engine.JRException {
        ClassPathResource resource = new ClassPathResource(MAIN_TEMPLATE);
        try (InputStream is = resource.getInputStream()) {
            return JasperCompileManager.compileReport(is);
        }
    }

    private Map<String, Object> buildParameters(LoadedSaleDocument document) {
        Map<String, Object> params = new HashMap<>();
        params.put("DOCUMENT_UID", document.getDocumentUid());
        params.put("TICKET_CODE", safeText(document.getTicketCode()));
        params.put("CUSTOMER_NAME", safeText(document.getCustomerName()));
        params.put("TOTAL_AMOUNT", safeAmount(document.getTotalAmount()));
        params.put("DEVOLUCION", document.isRefund());
        params.put("LINEAS_AGRUPADAS", document.isGroupedLines());
        params.put("SUBREPORT_DIR", "informes/ventas/facturas/");
        params.put("PAYMENTS", mapPayments(document.getPayments()));
        params.put("PROMOTIONS", mapPromotions(document.getPromotions()));
        CardPaymentData cardPaymentData = document.getCardPaymentData();
        if (cardPaymentData != null) {
            params.put("CARD_NUMBER", cardPaymentData.getCardNumber());
            params.put("CARD_AUTH_CODE", cardPaymentData.getAuthorisationCode());
            params.put("CARD_TERMINAL", cardPaymentData.getTerminalId());
        }
        return params;
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private List<Map<String, Object>> mapPayments(List<PaymentDetail> payments) {
        return payments.stream()
                .map(payment -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", payment.getCode());
                    map.put("description", payment.getDescription());
                    map.put("amount", safeAmount(payment.getAmount()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> mapPromotions(List<PromotionDetail> promotions) {
        return promotions.stream()
                .map(promotion -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", promotion.getCode());
                    map.put("description", promotion.getDescription());
                    map.put("amount", safeAmount(promotion.getAmount()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    private String buildFileName(LoadedSaleDocument document) {
        return String.format("factura-%s.pdf", document.getDocumentUid());
    }
}
