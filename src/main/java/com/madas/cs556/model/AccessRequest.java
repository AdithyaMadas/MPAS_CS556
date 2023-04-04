package com.madas.cs556.model;

public class AccessRequest {
    Integer from;
    Integer to;
    Modes modes;

    public AccessRequest(Integer from, Integer to, String access) {
        this.from = from;
        this.to = to;
        this.modes = new Modes(access);
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

    public Modes getModes() {
        return modes;
    }
}
