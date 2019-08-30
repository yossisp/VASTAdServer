package com.vastserver.classes.JSONObjects;

import com.vastserver.classes.vastErrorHandling.VastEntityException;
import com.vastserver.interfaces.VastEntity;
import com.vastserver.classes.VastLogger;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

import static com.vastserver.config.VastConfig.emptyStr;

public class VastAdvertiser implements VastEntity {
    Logger log = VastLogger.getLogger(VastAdvertiser.class);

    private String advName;
    private float price; //CPM
    private String category;
    private int budget; //max impressions amount
    private String vastUrl;
    private String id;
    private boolean active;

    public VastAdvertiser() {

    }

    public VastAdvertiser(String advName, float price, String category,
                          int budget, String vastUrl, String id,
                          boolean active) throws VastEntityException {
        this.advName = advName;
        this.price = price;
        this.category = category;
        this.budget = budget;
        this.vastUrl = vastUrl;
        this.id = id;
        this.active = active;
        this.isValidatedEntity();
    }

    public boolean isValidatedEntity() throws VastEntityException {
        String validationResult = "";
        if (this.advName == null) {
            validationResult += "Advertiser name empty.\n";
        }

        if (this.category == null) {
            validationResult += "Category name empty.\n";
        }

        if (this.vastUrl == null) {
            validationResult += "Vast URL empty.\n";
            if (this.validateUrl() == null) {
                validationResult += "Invalid URL entered.\n";
            }
        }

        if (validationResult != emptyStr) {
            throw new VastEntityException(validationResult);
        }

        return true;
    }

    private URL validateUrl() {
        URL validatedUrl = null;
        try {
            validatedUrl = new URL(this.vastUrl);
        } catch (MalformedURLException e) {
            log.error("invalid URL");
            log.error(e.getMessage());
        }
        return validatedUrl;
    }

    public String getAdvName() {
        return advName;
    }

    public void setAdvName(String advName) {
        this.advName = advName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public String getVastUrl() {
        return vastUrl;
    }

    public void setVastUrl(String vastUrl) {
        this.vastUrl = vastUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "VastAdvertiser{" +
                ", advName='" + advName + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", budget=" + budget +
                ", vastUrl='" + vastUrl + '\'' +
                ", id='" + id + '\'' +
                ", active=" + active +
                '}';
    }
}
