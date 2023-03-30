package com.madas.cs556.services.authorizationServices.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Admins {
    private Integer uid;
    private final Set<Admins> delegateTo;

    private Integer delegatedBy;

    boolean isOwner;


    public Admins(int i) {
        uid = i;
        delegateTo = new HashSet<>();
        delegatedBy = 0;
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
                '}';
    }
}
