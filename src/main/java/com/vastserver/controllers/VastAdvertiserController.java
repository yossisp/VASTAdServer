package com.vastserver.controllers;

import com.vastserver.classes.vastErrorHandling.VastErrorResponse;
import com.vastserver.classes.*;
import com.vastserver.classes.JSONObjects.VastAdvertiser;
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

import static com.vastserver.config.Routes.advId;
import static com.vastserver.config.SQLConstants.*;
import static com.vastserver.config.VastErrorMessages.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class VastAdvertiserController {
    private static Logger log = VastLogger.getLogger(VastAdvertiserController.class);

    @Autowired
    private DBHandler db;

    @Autowired
    private RandomUUID uuid;

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=POST, path="/" + advId, consumes = APPLICATION_JSON_VALUE,
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAdvertiser(@RequestBody VastAdvertiser advFromRequest) {
        log.info(HttpMethod.POST.toString() + ": " + advFromRequest);

        if (advFromRequest.getAdvName() == "") {
            log.info("empty advName entered");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    entityNameMissing, noOtherErrors);
            return new ResponseEntity<>(
                    apiError, new HttpHeaders(), apiError.getStatus());
        }

        if (this.db.isEntityExists(advFromRequest.getAdvName(),
                advertisers, advName)) {
            log.info("isAdvertiserExists = true");
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    advAlreadyExists, noOtherErrors);
            return new ResponseEntity<>(
                    apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {
            VastAdvertiser advToDb = new VastAdvertiser(advFromRequest.getAdvName(),
                    advFromRequest.getPrice(),
                    advFromRequest.getCategory(),
                    advFromRequest.getBudget(),
                    advFromRequest.getVastUrl(),
                    this.uuid.getUuid(),
                    advFromRequest.isActive());

            db.insertVastAdvertiser(advToDb);
            return new ResponseEntity<>(advToDb, new HttpHeaders(), HttpStatus.CREATED);
        } catch (VastEntityException e) {
            log.error(e.getMessage());
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    e.getMessage(), noOtherErrors);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=PUT, path=("/" + advId),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changeAdvertiser(
            @RequestBody Map<String, Object> update) {

        log.info(HttpMethod.PUT.toString() + ": " + update);
        VastAdvertiser updatedAdv = null;

        if (update.get(SQLConstants.id) == null) {
            VastErrorResponse response = new VastErrorResponse(HttpStatus.BAD_REQUEST,
                    entityIdMissing,
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        String advertiserId = update.get(SQLConstants.id).toString();
        try {
            /*
            findOneVastAdv throws exception if adv is not found,
            it's a quick way to make sure the adv exists
             */
            db.findOneVastAdv(advertiserId);

            db.updateEntity(update, advertisers, id);
            updatedAdv = db.findOneVastAdv(advertiserId);
        } catch (Exception e) {
            log.error(e.getMessage());
            VastErrorResponse response = new VastErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage(),
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        return new JSONResponse(updatedAdv, new HttpHeaders(), HttpStatus.OK);
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=GET, path=("/advId/{advId}"),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAdvertiser(
            @PathVariable("advId") String advId) {

        log.info(HttpMethod.GET.toString() + ": " + advId);
        VastAdvertiser adv = null;

        try {
            adv = db.findOneVastAdv(advId);
        } catch (Exception e) {
            log.error(e.getMessage());
            VastErrorResponse response = new VastErrorResponse(HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    noOtherErrors, new HttpHeaders());
            return response.getErrorResponse();
        }

        return new JSONResponse(adv, new HttpHeaders(), HttpStatus.OK);
    }

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=DELETE, path=("/advId/{advId}"),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteAdvertiser(
            @PathVariable("advId") String advId) {
        log.info(HttpMethod.DELETE.toString() + "advId: " + advId);

        try {
            this.db.deleteOneAdvertiser(advId);
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
    @RequestMapping(method=GET, path=("/advId/all"),
            produces=APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllAdvertisers() {
        List<VastAdvertiser> list = this.db.findAllAdvertisers();
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
