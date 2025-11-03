package com.comerzzia.bricodepot.api.omnichannel.service.salesdocument;

import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.loader.dto.LoadedSaleDocument;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report.SaleDocumentReport;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report.SaleDocumentReportAssembler;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.repository.SaleDocumentRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import javax.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleDocumentServiceImpl implements SaleDocumentService {

    private final SaleDocumentRepository repository;
    private final SaleDocumentReportAssembler reportAssembler;

    public SaleDocumentServiceImpl(SaleDocumentRepository repository,
                                   SaleDocumentReportAssembler reportAssembler) {
        this.repository = repository;
        this.reportAssembler = reportAssembler;
    }

    @Override
    @Transactional(readOnly = true)
    public SaleDocumentReport printSalesDocument(String activityUid, String documentUid) {
        try {
            LoadedSaleDocument document = repository.loadSaleDocument(activityUid, documentUid);
            return reportAssembler.buildReport(document);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(String.format("Document %s not found", documentUid));
        }
    }
}
