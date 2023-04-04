package com.madas.cs556.services.authorizationServices;

import com.madas.cs556.model.AccessRequest;
import com.madas.cs556.services.authorizationServices.constants.TransferMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationService {

    @Autowired
    ObjectPermission tables;

    public void createTable(String name, List<Integer> owners, boolean isOwnershipReq, Integer quorum) {
        tables.addTable(name, owners, isOwnershipReq, quorum);
    }

    public String printAcceptanceStatus(String name) {
        return tables.getTable(name).printAcceptance();
    }

    public String acceptOwnership(String tableName, Integer to) {
        return tables.getTable(tableName).acceptOwnership(to).getDescription();
    }


    public String printAdmins(String name) {
        return tables.getTable(name).printAdmins();
    }
    public String printAccess(String name) {
        return tables.getTable(name).printAccess();
    }

    public String transferOwnership(String tableName, Integer from, Integer to, TransferMode mode) {
        return tables.getTable(tableName).transferOwnership(from, to, mode).getDescription();
    }

    public String delegateFromTo(String tableName, Integer from, Integer to) {
        return tables.getTable(tableName).delegateFromTo(from, to).getDescription();
    }
    public String removeDelegation(String tableName, Integer from, Integer to) {
        return tables.getTable(tableName).removeDelegation(from, to).getDescription();
    }

    public String giveAccess(String tableName, AccessRequest request) {
        return tables.getTable(tableName).giveAccess(request).getDescription();
    }

    public String revokeAccess(String tableName, AccessRequest request) {
        return tables.getTable(tableName).revokeAccess(request).getDescription();
    }

    public boolean doesUserHaveSelectAccess(String tableName, Integer uid) {
        return tables.getTable(tableName).doesUserHaveSelectAccess(uid);
    }
    public boolean doesUserHaveInsertAccess(String tableName, Integer uid) {
        return tables.getTable(tableName).doesUserHaveInsertAccess(uid);
    }
    public boolean doesUserHaveDeleteAccess(String tableName, Integer uid) {
        return tables.getTable(tableName).doesUserHaveDeleteAccess(uid);
    }
    public boolean doesUserHaveUpdateAccess(String tableName, Integer uid) {
        return tables.getTable(tableName).doesUserHaveUpdateAccess(uid);
    }
    public boolean doesUserHaveDropAccess(String tableName, Integer uid) {
        return tables.getTable(tableName).doesUserHaveDropAccess(uid);
    }

}
