package com.comerzzia.bricodepot.api.omnichannel.api.rest.salesdoc;

import com.comerzzia.bricodepot.api.omnichannel.api.rest.salesdoc.dto.PrintSalesDocumentResponse;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.SaleDocumentService;
import com.comerzzia.bricodepot.api.omnichannel.service.salesdocument.report.SaleDocumentReport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST endpoint that overrides the standard implementation to print sales documents using
 * the customised Bricodepot report pipeline.
 */
@Component
@Path("/salesdocument")
@Produces(MediaType.APPLICATION_JSON)
public class SalesDocumentResource {

    private final SaleDocumentService service;

    public SalesDocumentResource(SaleDocumentService service) {
        this.service = service;
    }

    @GET
    @Path("/{documentUid}/print")
    public Response print(@HeaderParam("X-Activity-Uid") String activityUid,
                          @PathParam("documentUid") String documentUid) {
        if (StringUtils.isBlank(activityUid)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required header X-Activity-Uid")
                    .build();
        }
        SaleDocumentReport report = service.printSalesDocument(activityUid, documentUid);
        PrintSalesDocumentResponse response = new PrintSalesDocumentResponse(
                report.getFileName(),
                report.getMimeType(),
                report.getData()
        );
        return Response.ok(response).build();
    }
}
