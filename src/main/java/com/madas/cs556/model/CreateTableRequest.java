package com.madas.cs556.model;

import java.util.List;

public class CreateTableRequest {
    String tableName;

    List<Integer> owners;

    boolean isOwnershipReq;

    public boolean isOwnershipReq() {
        return isOwnershipReq;
    }

    public void setOwnershipReq(boolean ownershipReq) {
        isOwnershipReq = ownershipReq;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Integer> getOwners() {
        return owners;
    }

    public void setOwners(List<Integer> owners) {
        this.owners = owners;
    }
}
