package com.madas.cs556.services.authorizationServices;

import com.madas.cs556.services.authorizationServices.constants.TransferMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationService {

    @Autowired
    ObjectPermission tables;

    public void createTable(String name, List<Integer> owners, boolean isOwnershipReq) {
        tables.addTable(name, owners, isOwnershipReq);
    }

    public String printAdmins(String name) {
        return tables.getTable(name).printAdmins();
    }

    public String printAcceptanceStatus(String name) {
        return tables.getTable(name).printAcceptance();
    }

    public String transferOwnership(String tableName, Integer from, Integer to, TransferMode mode) {
        return tables.getTable(tableName).transferOwnership(from, to, mode).getDescription();
    }

    public String acceptOwnership(String tableName, Integer to) {
        return tables.getTable(tableName).acceptOwnership(to).getDescription();
    }

    public String delegateFromTo(String tableName, Integer from, Integer to) {
        return tables.getTable(tableName).delegateFromTo(from, to).getDescription();
    }
    public String removeDelegation(String tableName, Integer from, Integer to) {
        return tables.getTable(tableName).removeDelegation(from, to).getDescription();
    }

}
