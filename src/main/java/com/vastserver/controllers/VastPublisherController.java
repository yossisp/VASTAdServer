package com.vastserver.controllers;

import com.vastserver.classes.vastErrorHandling.VastErrorResponse;
import com.vastserver.classes.*;
import com.vastserver.classes.JSONObjects.VastPublisher;
import com.vastserver.classes.vastErrorHandling.ApiError;
import com.vastserver.classes.JSONResponse;
import com.vastserver.classes.vastErrorHandling.VastEntityException;
import com.vastserver.config.SQLConstants;
import com.vastserver.config.VastConfig;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.vastserver.config.Routes.pubId;
import static com.vastserver.config.SQLConstants.*;
import static com.vastserver.config.VastErrorMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class VastPublisherController {
    private static Logger log = VastLogger.getLogger(VastPublisherController.class);

    @Autowired
    private DBHandler db;

    @Autowired
    private RandomUUID uuid;

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=POST, path=("/" + pubId), consumes = APPLICATION_JSON_VALUE,
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createPublisher(@RequestBody VastPublisher pubFromRequest) {
        log.info(HttpMethod.POST.toString() + ": " + pubFromRequest);

        if (pubFromRequest.getPubName() == "") {
            log.info("empty pubName entered");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    entityNameMissing, noOtherErrors);
            return new ResponseEntity<>(
                    apiError, new HttpHeaders(), apiError.getStatus());
        }

        if (this.db.isEntityExists(pubFromRequest.getPubName(),
                publishers, pubName)) {
            log.info("isPublisherExists = true");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    pubAlreadyExists, noOtherErrors);
            return new ResponseEntity<>(
                    apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {
            VastPublisher pubToDb = new VastPublisher(pubFromRequest.getPubName(),
                    pubFromRequest.getCategory(),
                    pubFromRequest.getCost(),
                    this.uuid.getUuid(),
                    pubFromRequest.isActive());
            db.insertVastPublisher(pubToDb);

            return new ResponseEntity<>(pubToDb, new HttpHeaders(), HttpStatus.CREATED);
        } catch (VastEntityException e) {
            log.error(e.getMessage());
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    e.getMessage(), noOtherErrors);
            return new ResponseEntity<>(
                    apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=PUT, path=("/" + pubId),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changePublisher(
            @RequestBody Map<String, Object> update) {

        log.info(HttpMethod.PUT.toString() + ": " + update);
        VastPublisher updatedPub = null;

        if (update.get(SQLConstants.id) == null) {
            VastErrorResponse response = new VastErrorResponse(HttpStatus.BAD_REQUEST,
                    entityIdMissing,
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        try {
            /*
            findOneVastPub throws exception if pub is not found,
            it's a quick way to make sure the pub exists
             */
            db.findOneVastPub(update.get(SQLConstants.id).toString());

            db.updateEntity(update, publishers, id);
            updatedPub = db.findOneVastPub(update.get(SQLConstants.id).toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            VastErrorResponse response = new VastErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        return new JSONResponse(updatedPub, new HttpHeaders(), HttpStatus.OK);
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=GET, path=("/pubId/{pubId}"),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPublisher(
            @PathVariable("pubId") String pubId) {

        log.info(HttpMethod.GET.toString() + ": " + pubId);
        VastPublisher pub = null;

        try {
            pub = db.findOneVastPub(pubId);
        } catch (Exception e) {
            log.error(e.getMessage());
            VastErrorResponse response = new VastErrorResponse(HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        return new JSONResponse(pub, new HttpHeaders(), HttpStatus.OK);
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=DELETE, path=("/pubId/{pubId}"),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deletePublisher(
            @PathVariable("pubId") String pubId) {
        log.info(HttpMethod.DELETE.toString() + " pubId: " + pubId);

        try {
            this.db.deleteOnePublisher(pubId);
        } catch (Exception e) {
            log.error(e.getMessage());
            VastErrorResponse response = new VastErrorResponse(HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        VastErrorResponse response = new VastErrorResponse(HttpStatus.NO_CONTENT,
                entitySuccessfullyDeleted,
                noOtherErrors, new HttpHeaders());
        return response.getErrorResponse();
    }

   @CrossOrigin(origins = VastConfig.allowedSingleDomain)
   @RequestMapping(method=GET, path=("/pubId/all"),
            produces=APPLICATION_JSON_VALUE)
   public ResponseEntity<Object> getAllPublishers() {
        List<VastPublisher> list = this.db.findAllPublishers();
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
   }
}