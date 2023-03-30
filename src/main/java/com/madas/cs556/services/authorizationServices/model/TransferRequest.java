package com.madas.cs556.services.authorizationServices.model;

import com.madas.cs556.services.authorizationServices.constants.TransferMode;

public class TransferRequest {
    private int from;
    private int to;
    private TransferMode requestType;

    public TransferRequest(int from, int to, TransferMode requestType) {
        this.from = from;
        this.to = to;
        this.requestType = requestType;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public TransferMode getRequestType() {
        return requestType;
    }

    public void setRequestType(TransferMode requestType) {
        this.requestType = requestType;
    }
}
