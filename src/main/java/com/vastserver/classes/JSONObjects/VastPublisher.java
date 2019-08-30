package com.vastserver.classes.JSONObjects;

import com.vastserver.classes.vastErrorHandling.VastEntityException;
import com.vastserver.interfaces.VastEntity;

import static com.vastserver.config.VastConfig.emptyStr;

public class VastPublisher implements VastEntity {
    private String pubName;
    private String category;
    private float cost;
    private String id;
    private boolean active;

    // empty default constructor is required for Jackson
    public VastPublisher() {

    }

    public VastPublisher(String pubName, String category, float cost, String id,
                         boolean active)
    throws VastEntityException {
        this.pubName = pubName;
        this.category = category;
        this.cost = cost;
        this.id = id;
        this.active = active;
        this.isValidatedEntity();
    }

    public boolean isValidatedEntity() throws VastEntityException {
        String validationResult = "";
        if (this.pubName == null) {
            validationResult += "Publisher name empty.\n";
        }

        if (this.category == null) {
            validationResult += "Category name empty.\n";
        }

        if (validationResult != emptyStr) {
            throw new VastEntityException(validationResult);
        }

        return true;
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
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
        return "VastPublisher{" +
                "pubName='" + pubName + '\'' +
                ", category='" + category + '\'' +
                ", cost=" + cost +
                ", id='" + id + '\'' +
                ", active=" + active +
                '}';
    }
}
