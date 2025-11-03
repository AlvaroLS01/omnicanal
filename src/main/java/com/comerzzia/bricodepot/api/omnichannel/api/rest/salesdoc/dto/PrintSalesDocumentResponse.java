package com.comerzzia.bricodepot.api.omnichannel.api.rest.salesdoc.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple DTO wrapping the printable report payload so the API contract remains explicit.
 */
public class PrintSalesDocumentResponse {

    private final String fileName;
    private final String mimeType;
    private final String data;

    @JsonCreator
    public PrintSalesDocumentResponse(@JsonProperty("fileName") String fileName,
                                      @JsonProperty("mimeType") String mimeType,
                                      @JsonProperty("data") String data) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getData() {
        return data;
    }
}
