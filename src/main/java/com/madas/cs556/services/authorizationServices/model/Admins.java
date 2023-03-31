package com.madas.cs556.services.authorizationServices.model;

import java.util.*;

public class Admins {
    private Integer uid;
    private final Set<Admins> delegateTo;

    private HashMap<Integer, Integer> delegatedByOwners;

    boolean isOwner;


    public Admins(int i) {
        uid = i;
        delegateTo = new HashSet<>();
        delegatedByOwners = new HashMap<>();
    }

    public List<Admins> getDelegateTo() {
        return new ArrayList<>(delegateTo);
    }

    public void addToDelegates(Admins to) {
        if (containsDelegate(to)) {
            return;
        }
        Set<Admins> visitedAdmins = new HashSet<>();
        visitedAdmins.add(this);
        to.addDelegation(this, visitedAdmins);
        delegateTo.add(to);
    }

    public void addDelegation(Admins from, Set<Admins> visited) {
        if (visited.contains(this)) {
            return;
        }
        visited.add(this);
        for (Integer owner : from.delegatedByOwners.keySet()) {
            Integer prevCount = delegatedByOwners.getOrDefault(owner, 0);
            delegatedByOwners.put(owner, prevCount + from.delegatedByOwners.get(owner));
        }
        for (Admins delegatedTo : delegateTo) {
            delegatedTo.addDelegation(this, visited);
        }
    }

    public void transferDelegates(Admins to) {
        delegateTo.add(to);
    }

    public boolean containsDelegate(Admins to) {
        return delegateTo.contains(to);
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
        delegatedByOwners.put(uid, 1);
    }

    public void removeDelegate(Admins toAdmin) {
        delegateTo.remove(toAdmin);
        toAdmin.removeOwners(this.delegatedByOwners);
    }

    public void removeOwners(HashMap<Integer, Integer> fromOwners) {
        for (Integer owner : fromOwners.keySet()) {
            Integer prevCount = delegatedByOwners.get(owner);
            Integer newCount = prevCount - fromOwners.get(owner);
            if (newCount <= 0) {
                delegatedByOwners.remove(owner);
            } else {
                delegatedByOwners.put(owner, newCount);
            }
        }
    }

    public int getDelegatedBy() {
        return delegatedByOwners.size();
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
                "], delegatedByOwners=" + delegatedByOwners +
                ", isOwner=" + isOwner +
                '}';
    }

}
