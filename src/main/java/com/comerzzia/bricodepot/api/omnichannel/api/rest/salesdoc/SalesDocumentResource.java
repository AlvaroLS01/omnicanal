package com.comerzzia.bricodepot.api.omnichannel.api.rest.salesdoc;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.comerzzia.api.core.service.exception.ApiException;
import com.comerzzia.api.core.service.util.ComerzziaDatosSesion;
import com.comerzzia.api.omnichannel.web.model.document.PrintDocumentRequest;
import com.comerzzia.bricodepot.api.omnichannel.api.model.salesdoc.SalesDocumentPrintResponse;
import com.comerzzia.omnichannel.domain.dto.saledoc.PrintDocumentDTO;
import com.comerzzia.omnichannel.service.salesdocument.SaleDocumentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/salesdocument")
@Tag(name = "Sales documents", description = "Sales documents services")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Controller
public class SalesDocumentResource {

    private static final String DEFAULT_PDF_MIME = "application/pdf";

    @Autowired
    private SaleDocumentService service;

    @Autowired
    protected ComerzziaDatosSesion datosSesionRequest;

    @Autowired
    protected ModelMapper modelMapper;

    @GET
    @Path("/{documentUid}/print")
    @Operation(summary = "Print sales document by uid", description = "Print sales document by uid", responses = {
            @ApiResponse(description = "The print output",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = SalesDocumentPrintResponse.class)),
                            @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "byte")),
                            @Content(mediaType = "application/jasperprint", schema = @Schema(type = "string", format = "byte")),
                            @Content(mediaType = "text/html", schema = @Schema(type = "string", format = "byte"))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid input values"),
            @ApiResponse(responseCode = "404", description = "Record not found") })
    @Produces(MediaType.APPLICATION_JSON)
    public Response printSaleDocumentByUid(@PathParam("documentUid")
    String documentUid,
            @Context HttpServletRequest request,
            @Valid
            @BeanParam
            PrintDocumentRequest
            printDocumentRequest) throws ApiException {

        if (StringUtils.isEmpty(printDocumentRequest.getMimeType())) {
            printDocumentRequest.setMimeType(request.getContentType());
        }

        if (StringUtils.isEmpty(printDocumentRequest.getMimeType())) {
            printDocumentRequest.setMimeType(DEFAULT_PDF_MIME);
        }

        if (StringUtils.isEmpty(printDocumentRequest.getOutputDocumentName())) {
            printDocumentRequest.setOutputDocumentName(documentUid);
        }

        if (printDocumentRequest.getInline() == null) {
            printDocumentRequest.setInline(false);
        }

        PrintDocumentDTO printDocumentDTO = modelMapper.map(printDocumentRequest, PrintDocumentDTO.class);

        if (StringUtils.isEmpty(printDocumentDTO.getMimeType())) {
            printDocumentDTO.setMimeType(DEFAULT_PDF_MIME);
        }

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            service.printDocument(output, datosSesionRequest.getDatosSesionBean(), documentUid, printDocumentDTO);

            output.flush();
            String base64 = Base64.getEncoder().encodeToString(output.toByteArray());

            SalesDocumentPrintResponse payload = new SalesDocumentPrintResponse(printDocumentDTO.getMimeType(),
                    buildFileName(printDocumentDTO.getOutputDocumentName(), printDocumentDTO.getMimeType()), base64);

            return Response.ok(payload, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new WebApplicationException("Error creating pdf !!. Message: " + e.getMessage(), e);
        }
    }

    private String buildFileName(String outputDocumentName, String mimeType) {
        String extension = DEFAULT_PDF_MIME.equalsIgnoreCase(mimeType) ? ".pdf" : "";
        if (StringUtils.isEmpty(outputDocumentName)) {
            return "document" + extension;
        }
        if (StringUtils.endsWithIgnoreCase(outputDocumentName, extension)) {
            return outputDocumentName;
        }
        return outputDocumentName + extension;
    }
}
