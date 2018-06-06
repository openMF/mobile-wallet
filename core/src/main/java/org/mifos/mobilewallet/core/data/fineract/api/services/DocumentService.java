package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.noncore.Document;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */

public interface DocumentService {

    @GET("{entityType}/{entityId}/" + ApiEndPoints.DOCUMENTS)
    Observable<List<Document>> getDocuments(@Path("entityType") String entityType,
            @Path("entityId") int entityId);

    /**
     * @param entityType     - Type for which document is being uploaded (Client, Loan
     *                       or Savings etc)
     * @param entityId       - Id of Entity
     * @param nameOfDocument - Document Name
     * @param description    - Mandatory - Document Description
     * @param typedFile      - Mandatory
     */
    @POST("{entityType}/{entityId}/" + ApiEndPoints.DOCUMENTS)
    @Multipart
    Observable<GenericResponse> createDocument(
            @Path("entityType") String entityType,
            @Path("entityId") long entityId,
            @Part("name") String nameOfDocument,
            @Part("description") String description,
            @Part() MultipartBody.Part typedFile);


    /**
     * This Service is for downloading the Document with EntityType and EntityId and Document Id
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}/attachment
     *
     * @param entityType - Type for which document is being uploaded (Client, Loan
     *                   or Savings etc)
     * @param entityId   - Id of Entity
     * @param documentId - Document Id
     * @return ResponseBody
     */
    @GET("{entityType}/{entityId}/" + ApiEndPoints.DOCUMENTS + "/{documentId}/attachment")
    Observable<ResponseBody> downloadDocument(@Path("entityType") String entityType,
            @Path("entityId") int entityId,
            @Path("documentId") int documentId);

    /**
     * This Service is for Deleting the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType - Type for which document is being uploaded (Client, Loan
     *                   or Savings etc)
     * @param entityId   - Id of Entity
     * @param documentId - Document Id
     */
    @DELETE("{entityType}/{entityId}/" + ApiEndPoints.DOCUMENTS + "/{documentId}")
    Observable<ResponseBody> removeDocument(@Path("entityType") String entityType,
            @Path("entityId") int entityId,
            @Path("documentId") int documentId);

    /**
     * This Service for Updating the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * PUT
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType     - Type for which document is being uploaded (Client, Loan
     *                       or Savings etc)
     * @param entityId       - Id of Entity
     * @param documentId     - Id of document
     * @param nameOfDocument - Document Name
     * @param description    - Mandatory - Document Description
     * @param typedFile      - Mandatory
     */
    @PUT("{entityType}/{entityId}/" + ApiEndPoints.DOCUMENTS + "/{documentId}")
    @Multipart
    Observable<ResponseBody> updateDocument(@Path("entityType") String entityType,
            @Path("entityId") int entityId,
            @Path("documentId") int documentId,
            @Part("name") String nameOfDocument,
            @Part("description") String description,
            @Part() MultipartBody.Part typedFile);
}
