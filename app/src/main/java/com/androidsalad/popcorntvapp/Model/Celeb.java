package com.androidsalad.popcorntvapp.Model;

public class Celeb {

    private String celebId;
    private String celebName;
    private String celebFullUrl;
    private String celebThumbUrl;

    public Celeb() {
    }

    public Celeb(String celebId, String celebName, String celebFullUrl, String celebThumbUrl) {
        this.celebId = celebId;
        this.celebName = celebName;
        this.celebFullUrl = celebFullUrl;
        this.celebThumbUrl = celebThumbUrl;
    }

    public String getCelebId() {
        return celebId;
    }

    public void setCelebId(String celebId) {
        this.celebId = celebId;
    }

    public String getCelebName() {
        return celebName;
    }

    public void setCelebName(String celebName) {
        this.celebName = celebName;
    }

    public String getCelebFullUrl() {
        return celebFullUrl;
    }

    public void setCelebFullUrl(String celebFullUrl) {
        this.celebFullUrl = celebFullUrl;
    }

    public String getCelebThumbUrl() {
        return celebThumbUrl;
    }

    public void setCelebThumbUrl(String celebThumbUrl) {
        this.celebThumbUrl = celebThumbUrl;
    }
}
