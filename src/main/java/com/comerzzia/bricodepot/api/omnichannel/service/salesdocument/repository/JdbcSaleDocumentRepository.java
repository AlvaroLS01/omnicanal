package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.repository;

import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.CardPaymentData;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.LoadedSaleDocument;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.PaymentDetail;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.PromotionDetail;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcSaleDocumentRepository implements SaleDocumentRepository {

    private static final String SQL_TICKET = "SELECT COD_TICKET, DESTIPODOCUMENTO, FECHA FROM D_TICKETS WHERE UID_ACTIVIDAD = :activityUid AND UID_TICKET = :documentUid";
    private static final String SQL_PAYMENTS = "SELECT MEDPAG.CODMEDPAG, MEDPAG.DESMEDPAG, PAG.IMPORTE AS IMPORTE_TOTAL FROM D_CLIE_ALBARANES_CAB_TBL CAB " +
            "INNER JOIN D_CLIE_ALBARANES_PAG_TBL PAG ON (PAG.UID_ACTIVIDAD = CAB.UID_ACTIVIDAD AND PAG.ID_CLIE_ALBARAN = CAB.ID_CLIE_ALBARAN) " +
            "INNER JOIN D_MEDIOS_PAGO_VEN_TBL MEDPAGVEN ON (MEDPAGVEN.UID_ACTIVIDAD = PAG.UID_ACTIVIDAD AND MEDPAGVEN.ID_MEDPAG_VEN = PAG.ID_MEDPAG_VEN) " +
            "INNER JOIN D_MEDIOS_PAGO_TBL MEDPAG ON (MEDPAG.UID_ACTIVIDAD = PAG.UID_ACTIVIDAD AND MEDPAG.CODMEDPAG = MEDPAGVEN.CODMEDPAG) " +
            "WHERE CAB.UID_ACTIVIDAD = :activityUid AND CAB.UID_TICKET = :documentUid ORDER BY MEDPAG.CODMEDPAG";
    private static final String SQL_PROMOTIONS = "SELECT COD_PROMOCION, DES_PROMOCION, IMPORTE FROM V_FACTURA_PROMOCIONES WHERE UID_ACTIVIDAD = :activityUid AND UID_TICKET = :documentUid";
    private static final String SQL_CARD_DATA = "SELECT NUM_TARJETA, COD_AUTORIZACION, TERMINAL FROM V_FACTURA_PAGOS_TARJETA WHERE UID_ACTIVIDAD = :activityUid AND UID_TICKET = :documentUid";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcSaleDocumentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LoadedSaleDocument loadSaleDocument(String activityUid, String documentUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("activityUid", activityUid)
                .addValue("documentUid", documentUid);

        Map<String, Object> ticket = jdbcTemplate.queryForMap(SQL_TICKET, params);
        List<PaymentDetail> payments = jdbcTemplate.query(SQL_PAYMENTS, params, (rs, rowNum) -> mapPayment(rs));
        List<PromotionDetail> promotions = jdbcTemplate.query(SQL_PROMOTIONS, params, (rs, rowNum) -> mapPromotion(rs));
        CardPaymentData cardPaymentData = fetchCardPaymentData(params);

        String ticketCode = (String) ticket.get("COD_TICKET");
        String documentTypeDescription = (String) ticket.get("DESTIPODOCUMENTO");
        OffsetDateTime date = toOffsetDateTime(ticket.get("FECHA"));
        BigDecimal total = payments.stream()
                .map(PaymentDetail::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new LoadedSaleDocument(
                activityUid,
                documentUid,
                ticketCode,
                documentTypeDescription,
                date,
                total,
                null,
                payments,
                promotions,
                cardPaymentData,
                false,
                false
        );
    }

    private PaymentDetail mapPayment(ResultSet rs) throws SQLException {
        return new PaymentDetail(
                rs.getString("CODMEDPAG"),
                rs.getString("DESMEDPAG"),
                rs.getBigDecimal("IMPORTE_TOTAL"));
    }

    private PromotionDetail mapPromotion(ResultSet rs) throws SQLException {
        return new PromotionDetail(
                rs.getString("COD_PROMOCION"),
                rs.getString("DES_PROMOCION"),
                rs.getBigDecimal("IMPORTE"));
    }

    private CardPaymentData fetchCardPaymentData(MapSqlParameterSource params) {
        try {
            return jdbcTemplate.queryForObject(SQL_CARD_DATA, params, (rs, rowNum) ->
                    new CardPaymentData(
                            rs.getString("NUM_TARJETA"),
                            rs.getString("COD_AUTORIZACION"),
                            rs.getString("TERMINAL")));
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toInstant().atOffset(ZoneOffset.UTC);
        }
        if (value instanceof java.util.Date) {
            return ((java.util.Date) value).toInstant().atOffset(ZoneOffset.UTC);
        }
        return null;
    }
}
