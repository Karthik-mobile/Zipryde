package com.trivectadigital.ziprydedriverapp.assist;

/**
 * Created by Hari on 18-06-2017.
 */

public class CommissionDetails {

    private String commissionId, commissionContent, commissionTime;

    public CommissionDetails(String commissionId, String commissionContent, String commissionTime) {
        this.commissionId = commissionId;
        this.commissionContent = commissionContent;
        this.commissionTime = commissionTime;
    }

    public String getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(String commissionId) {
        this.commissionId = commissionId;
    }

    public String getCommissionContent() {
        return commissionContent;
    }

    public void setCommissionContent(String commissionContent) {
        this.commissionContent = commissionContent;
    }

    public String getCommissionTime() {
        return commissionTime;
    }

    public void setCommissionTime(String commissionTime) {
        this.commissionTime = commissionTime;
    }
}
