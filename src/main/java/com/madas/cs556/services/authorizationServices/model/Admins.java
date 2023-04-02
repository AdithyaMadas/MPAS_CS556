package com.madas.cs556.services.authorizationServices.model;

import java.util.*;

public class Admins {
    private Integer uid;
    private final Set<Admins> delegateTo;

    private Integer delegatedBy;

    boolean isOwner;

    Map<Integer, Integer> ownerRelation;


    public Admins(int i) {
        uid = i;
        delegateTo = new HashSet<>();
        delegatedBy = 0;
        ownerRelation = new HashMap<>();
    }

    public Map<Integer, Integer> getOwnerRelation() {
        return ownerRelation;
    }

    public void addOwner(Map<Integer, Integer> owners) {
        for (Map.Entry<Integer, Integer> entry : owners.entrySet()) {
            Integer owner = entry.getKey();
            Integer ownerCount = this.ownerRelation.getOrDefault(owner, 0) + entry.getValue();
            ownerRelation.put(owner, ownerCount);
        }
    }

    public void addOwner(Integer owner) {
        ownerRelation.put(owner, 1);
    }

    public void removeOwner(Map<Integer, Integer> owners) {
        for (Map.Entry<Integer, Integer> entry : owners.entrySet()) {
            Integer owner = entry.getKey();
            Integer ownerCount = this.ownerRelation.get(owner) - entry.getValue();
            if (ownerCount == 0) {
                ownerRelation.remove(owner);
            } else {
                ownerRelation.put(owner, ownerCount);
            }
        }
    }

    public void replaceOwner(Integer from, Integer to) {
        Integer ownerCount = this.ownerRelation.get(from);
        if (ownerCount == null) {
            return;
        }
        this.ownerRelation.remove(from);
        this.ownerRelation.put(to, ownerCount);
    }

    public List<Integer> getOwnerList() {
        return new ArrayList<>(ownerRelation.keySet());
    }

    public List<Admins> getDelegateTo() {
        return new ArrayList<>(delegateTo);
    }

    public void addToDelegates(Admins to) {
        if (containsDelegate(to)) {
            return;
        }
        to.delegatedBy++;
        delegateTo.add(to);
    }

    public void transferDelegates(Admins to) {
        delegateTo.add(to);
    }

    public void reduceDelegateCount() {
        delegatedBy--;
    }

    public boolean containsDelegate(Admins to) {
        return delegateTo.contains(to);
    }
    public Integer getDelegatedBy() {
        return delegatedBy;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void removeDelegate(Admins toAdmin) {
        delegateTo.remove(toAdmin);
        toAdmin.delegatedBy--;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Admins i : delegateTo) {
            sb.append(i.getUid());
            sb.append(",");
        }
        return "Admins{" +
                "uid=" + uid +
                ", delegateTo=[" + sb +
                "], delegatedBy=" + delegatedBy +
                ", isOwner=" + isOwner +
                ", ownerRelation=" + ownerRelation +
                '}';
    }
}
