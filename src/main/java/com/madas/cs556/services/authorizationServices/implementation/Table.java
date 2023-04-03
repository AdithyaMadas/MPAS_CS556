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

    Set<Integer> owners;

    public boolean isOwnershipAcceptanceReq;
    public Table(String name, List<Integer> owners, boolean isOwnershipAcceptanceReq) {
        this.tableName = name;
//        adminDelegations = new HashSet<>();
        uidAdminMap = new HashMap<>();
        this.owners = new HashSet<>();
        for (int o : owners) {
            Admins admin = new Admins(o);
            admin.setIsOwner(true);
            uidAdminMap.put(o, admin);
            admin.addOwner(o);
            this.owners.add(o);
//            adminDelegations.add(admin);
        }
        this.stillToAcceptOwnership = new HashMap<>();
        this.isOwnershipAcceptanceReq = isOwnershipAcceptanceReq;
    }

    public StatusCode delegateFromTo(Integer from, Integer to) {
        if (from.equals(to)) {
            return StatusCode.TRYING_TO_DELEGATE_ITSELF;
        }
        if (uidAdminMap.containsKey(from)) {
            Admins fromAdmin = uidAdminMap.get(from);
            Admins toAdmin = uidAdminMap.getOrDefault(to, new Admins(to));
            uidAdminMap.put(to, toAdmin);
            if (fromAdmin.containsDelegate(toAdmin)) {
                return StatusCode.SUCCESS;
            }
            if (createsCycle(toAdmin, fromAdmin)) {
                return StatusCode.CREATES_CYCLE;
            }
            fromAdmin.addToDelegates(toAdmin);
            updateOwnersForChildren(toAdmin, fromAdmin.getOwnerRelation());
            return StatusCode.SUCCESS;
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }

    private boolean createsCycle(Admins toAdmin, Admins from) {
        if (toAdmin.containsDelegate(from)) {
            return true;
        }
        for (Admins i : toAdmin.getDelegateTo()) {
            if (createsCycle(i, from)) {
                return true;
            }
        }
        return false;
    }

    public StatusCode removeDelegation(Integer from, Integer to) {
        if (uidAdminMap.containsKey(from)) {
            if (uidAdminMap.containsKey(to)) {
                Admins fromAdmin = uidAdminMap.get(from);
                Admins toAdmin = uidAdminMap.get(to);
                if (fromAdmin.containsDelegate(toAdmin)) {
                    fromAdmin.removeDelegate(toAdmin);
                    removeDelegation(toAdmin, fromAdmin.getOwnerRelation());
//                    for (Admins delegates : toAdmin.getDelegateTo()) {
////                        if (!toAdmin.getIsOwner()) {
//                            removeDelegation(toAdmin.getUid(), delegates.getUid());
////                        }
//                    }
//                    if (toAdmin.getDelegatedBy() <= 0 && !toAdmin.getIsOwner()) {
//                        logger.info("User " + to + " is not an admin anymore");
//                        uidAdminMap.remove(to);
//                    }
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

    private void removeDelegation(Admins to, Map<Integer, Integer> ownerRelation) {
        for (Admins i : to.getDelegateTo()) {
            removeDelegation(i, ownerRelation);
        }
        to.removeOwner(ownerRelation);
        if (to.getOwnerRelation().size() == 0) {
            logger.info("User " + to + " is not an admin anymore");
            uidAdminMap.remove(to.getUid());
        }
    }

    private void updateOwnersForChildren(Admins to, Map<Integer, Integer> ownerRelation) {
        to.addOwner(ownerRelation);
        for (Admins i : to.getDelegateTo()) {
            updateOwnersForChildren(i, ownerRelation);
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

                toOwner.addOwner(fromOwner.getOwnerRelation());

                changeAllOwners(from, to);
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

    private void changeAllOwners(Integer from, Integer to) {
        for (Admins admin : uidAdminMap.values()) {
            admin.replaceOwner(from, to);
        }
        owners.remove(from);
        owners.add(to);
    }

    private void transferPreviousDelegations(List<Admins> delegateTo, Admins toOwner) {
        for (Admins admin : delegateTo) {
            if (!toOwner.containsDelegate(admin) && !admin.getUid().equals(toOwner.getUid())) {
                toOwner.transferDelegates(admin);
            } else {
//                admin.reduceDelegateCount();
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
        sb.append("Owners: ").append(owners);
        return sb.toString();
    }

    public String printAcceptance() {
        return stillToAcceptOwnership.toString();
    }

}