package com.madas.cs556.services.authorizationServices.implementation;

import com.madas.cs556.services.authorizationServices.constants.StatusCode;
import com.madas.cs556.services.authorizationServices.constants.TransferMode;
import com.madas.cs556.services.authorizationServices.model.Admins;
import com.madas.cs556.services.authorizationServices.model.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Table {


    //Todo: add loggers, use context logging, add Transaction ID in controller

    Logger logger = LoggerFactory.getLogger(Table.class);

    String tableName;
//    Set<Admins> adminDelegations;

    Map<Integer, Admins> uidAdminMap;
    Map<Integer, List<TransferRequest>> stillToAcceptOwnership;

    public boolean isOwnershipAcceptanceReq;
    public Table(String name, List<Integer> owners, boolean isOwnershipAcceptanceReq) {
        this.tableName = name;
//        adminDelegations = new HashSet<>();
        uidAdminMap = new HashMap<>();
        for (int o : owners) {
            Admins admin = new Admins(o);
            admin.setIsOwner(true);
            uidAdminMap.put(o, admin);
//            adminDelegations.add(admin);
        }
        this.stillToAcceptOwnership = new HashMap<>();
        this.isOwnershipAcceptanceReq = isOwnershipAcceptanceReq;
    }

    public StatusCode delegateFromTo(Integer from, Integer to) {
        if (uidAdminMap.containsKey(from)) {
            Admins fromAdmin = uidAdminMap.get(from);
            Admins toAdmin = uidAdminMap.getOrDefault(to, new Admins(to));
            uidAdminMap.put(to, toAdmin);
            fromAdmin.addToDelegates(toAdmin);
            return StatusCode.SUCCESS;
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }

    public StatusCode removeDelegation(Integer from, Integer to) {
        if (uidAdminMap.containsKey(from)) {
            if (uidAdminMap.containsKey(to)) {
                Admins fromAdmin = uidAdminMap.get(from);
                Admins toAdmin = uidAdminMap.get(to);
                if (fromAdmin.containsDelegate(toAdmin)) {
                    fromAdmin.removeDelegate(toAdmin);
                    for (Admins delegates : toAdmin.getDelegateTo()) {
                        if (!toAdmin.getIsOwner()) {
                            removeDelegation(toAdmin.getUid(), delegates.getUid());
                        }
                    }
                    if (toAdmin.getDelegatedBy() <= 0 && !toAdmin.getIsOwner()) {
                        logger.info("User " + to + " is not an admin anymore");
                        uidAdminMap.remove(to);
                    }
                    return StatusCode.SUCCESS;
                } else {
                    return StatusCode.DELEGATION_NOT_PROVIDED_BEFORE;
                }
            } else {
                return StatusCode.ADMIN_ACCESS_NOT_ASSIGNED;
            }
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }


    public StatusCode transferOwnership(Integer from, Integer to, TransferMode mode) {
        if (isOwnershipAcceptanceReq) {
            stillToAcceptOwnership.computeIfAbsent(to, v -> new ArrayList<>()).add(new TransferRequest(from, to, mode));
            return StatusCode.SUCCESS;
        } else {
            return transferOwnershipImpl(from, to, mode);
        }
    }

    //if the from is not an owner anymore, then it is just ignored.
    public StatusCode acceptOwnership(Integer to) {
        if (isOwnershipAcceptanceReq) {
            List<TransferRequest> transferRequests = stillToAcceptOwnership.get(to);
            if (transferRequests == null) {
                return StatusCode.NO_ONE_ASSIGNED_OWNERSHIP;
            }
            boolean isNewOwner = false;
            for (TransferRequest request : transferRequests) {
                if (uidAdminMap.containsKey(request.getFrom())) {
                    isNewOwner = true;
                    transferOwnershipImpl(request.getFrom(), request.getTo(), request.getRequestType());
                }
            }

            if (isNewOwner) {
                uidAdminMap.get(to).setIsOwner(true);
            }
            stillToAcceptOwnership.remove(to);
            return StatusCode.SUCCESS;
        } else {
            return StatusCode.MODE_NOT_SUPPORTED;
        }
    }
    private StatusCode transferOwnershipImpl(Integer from, Integer to, TransferMode mode) {
        if (uidAdminMap.containsKey(from)) {
            Admins fromOwner = uidAdminMap.get(from);
            if (fromOwner.getIsOwner()) {

                Admins toOwner = uidAdminMap.getOrDefault(to, new Admins(to));

                if (mode == TransferMode.RECURSIVE_REVOKE) {
                    for (Admins delegates : fromOwner.getDelegateTo()) {
                        removeDelegation(fromOwner.getUid(), delegates.getUid());
                    }
                } else if (mode == TransferMode.GRANTOR_TRANSFER) {
                    transferPreviousDelegations(fromOwner.getDelegateTo(), toOwner);
                }

                toOwner.setIsOwner(true);
                uidAdminMap.put(to, toOwner);
                if (fromOwner.getDelegatedBy() <= 0) {
                    uidAdminMap.remove(from);
                }else{
                    fromOwner.setIsOwner(false);
                }
                return StatusCode.SUCCESS;

            } else {
                return StatusCode.IS_NOT_AN_OWNER;
            }
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }

    private void transferPreviousDelegations(List<Admins> delegateTo, Admins toOwner) {
        for (Admins admin : delegateTo) {
            if (!toOwner.containsDelegate(admin) && !admin.getUid().equals(toOwner.getUid())) {
                toOwner.transferDelegates(admin);
            } else {
                admin.reduceDelegateCount();
            }
        }
    }

    public String printAdmins() {
        StringBuilder sb = new StringBuilder();
        for (Admins admins : uidAdminMap.values()) {
            System.out.println(admins);
            sb.append(admins);
            sb.append("\n");
        }
        return sb.toString();
    }

    public String printAcceptance() {
        return stillToAcceptOwnership.toString();
    }

}