package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report;

public class SaleDocumentReport {

    private final String fileName;
    private final String mimeType;
    private final String data;

    public SaleDocumentReport(String fileName, String mimeType, String data) {
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
