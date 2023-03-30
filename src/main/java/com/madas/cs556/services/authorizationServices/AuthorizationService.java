package com.madas.cs556.services.authorizationServices;

import com.madas.cs556.services.authorizationServices.constants.TransferMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationService {

    @Autowired
    ObjectPermission tables;

    public void createTable(String name, List<Integer> owners) {
        tables.addTable(name, owners);
    }

    public String printAdmins(String name) {
        return tables.getTable(name).printAdmins();
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

}
