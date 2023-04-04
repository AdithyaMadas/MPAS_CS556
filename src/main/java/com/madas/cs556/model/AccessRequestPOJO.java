package com.madas.cs556.model;

public class AccessRequestPOJO {
    Integer from, to;
    String modes, tableName;

    public AccessRequestPOJO(String tableName, Integer from, Integer to, String modes) {
        this.tableName = tableName;
        this.from = from;
        this.to = to;
        this.modes = modes;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String getModes() {
        return modes;
    }

    public void setModes(String modes) {
        this.modes = modes;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
