package com.madas.cs556.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccessControl {
    Map<Integer, Integer>[] accessMap;

    public AccessControl() {
        this.accessMap = new Map[5];
        for (int i = 0; i < 5; i++) {
            accessMap[i] = new HashMap<>();
        }
    }

    public void addToAccessControl(AccessRequest request, Map<Integer, Integer> ownerRelation) {
        for (int i = 0; i < 5; i++) {
            if (request.getModes().getAccessIndex()[i]) {
                Map<Integer, Integer> ownerMap = accessMap[i];
                for (Map.Entry<Integer, Integer> entry : ownerRelation.entrySet()) {
                    Integer newCount = ownerMap.getOrDefault(entry.getKey(), 0) + entry.getValue();
                    ownerMap.put(entry.getKey(), newCount);
                }
            }
        }
    }

    public void removeAccess(AccessRequest request, Map<Integer, Integer> ownerRelation) {
        for (int i = 0; i < 5; i++) {
            if (request.getModes().getAccessIndex()[i]) {
                Map<Integer, Integer> ownerMap = accessMap[i];
                for (Map.Entry<Integer, Integer> entry : ownerRelation.entrySet()) {
                    int newCount = ownerMap.get(entry.getKey()) - entry.getValue();
                    if (newCount == 0) {
                        ownerMap.remove(entry.getKey());
                    } else {
                        ownerMap.remove(entry.getKey());
                    }
                }
            }
        }
    }

    public int selectOwnerSize() {
        return accessMap[0].size();
    }
    public int insertOwnerSize() {
        return accessMap[1].size();
    }
    public int deleteOwnerSize() {
        return accessMap[2].size();
    }
    public int updateOwnerSize() {
        return accessMap[3].size();
    }
    public int dropOwnerSize() {
        return accessMap[4].size();
    }
    public boolean containsPermissions() {
        return selectOwnerSize() != 0 && insertOwnerSize() != 0 && deleteOwnerSize() != 0 && updateOwnerSize() != 0 && dropOwnerSize() != 0;
    }

    @Override
    public String toString() {
        return "AccessControl{" +
                "accessMap=" + Arrays.toString(accessMap) +
                '}';
    }

    public void updateOwner(Integer from, Integer to) {
        for (int i = 0; i < 5; i++) {
            Map<Integer, Integer> accessControl = accessMap[i];
            if (accessControl.containsKey(from)) {
                int newCount = accessControl.getOrDefault(to, 0) + accessControl.get(from);
                accessControl.put(to, newCount);
                accessControl.remove(from);
            }
        }
    }
}
