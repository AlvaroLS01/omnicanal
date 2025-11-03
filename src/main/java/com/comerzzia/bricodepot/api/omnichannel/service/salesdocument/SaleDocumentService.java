package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument;

import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report.SaleDocumentReport;

/**
 * Contract that exposes the customised report generation logic for sales documents.
 */
public interface SaleDocumentService {

    /**
     * Generates the printable representation (PDF encoded as Base64) of the provided document.
     *
     * @param activityUid identifier of the activity (tenant) owning the document.
     * @param documentUid identifier of the sales document to print.
     * @return the generated report metadata and payload.
     */
    SaleDocumentReport printSalesDocument(String activityUid, String documentUid);
}
