package com.madas.cs556.services.authorizationServices;

import com.madas.cs556.services.authorizationServices.implementation.Table;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ObjectPermission {


    Map<String, Table> mapping;

    public ObjectPermission() {
        this.mapping = new HashMap<>();
    }

    public void addTable(String name, List<Integer> owners, boolean isOwnershipReq) {
        Table table = new Table(name, owners, isOwnershipReq);
        mapping.put(name, table);
    }

    public Table getTable(String name) {
        return mapping.get(name);
    }

}
