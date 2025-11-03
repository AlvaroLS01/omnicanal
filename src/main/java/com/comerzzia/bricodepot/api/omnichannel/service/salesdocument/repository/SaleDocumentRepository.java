package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.repository;

import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.LoadedSaleDocument;

public interface SaleDocumentRepository {

    LoadedSaleDocument loadSaleDocument(String activityUid, String documentUid);
}
