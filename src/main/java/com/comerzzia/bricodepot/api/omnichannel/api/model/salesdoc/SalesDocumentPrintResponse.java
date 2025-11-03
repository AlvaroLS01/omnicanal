package com.comerzzia.bricodepot.api.omnichannel.api.model.salesdoc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response payload that wraps the generated document encoded in Base64 so it
 * can be handled easily by the omnichannel clients.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesDocumentPrintResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String mimeType;
    private final String fileName;
    private final String data;

    public SalesDocumentPrintResponse(String mimeType, String fileName, String data) {
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.data = data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getData() {
        return data;
    }
}
