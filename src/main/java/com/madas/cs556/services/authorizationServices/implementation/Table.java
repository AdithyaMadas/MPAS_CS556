package com.madas.cs556.services.authorizationServices.implementation;

import com.madas.cs556.model.AccessControl;
import com.madas.cs556.model.AccessRequest;
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

    Map<Integer, Admins> uidAdminMap;
    Map<Integer, List<TransferRequest>> stillToAcceptOwnership;

    Set<Integer> owners;

    Map<Integer, AccessControl> accessControlMap;

    public boolean isOwnershipAcceptanceReq;

    int quorumSize;

    public Table(String name, List<Integer> owners, boolean isOwnershipAcceptanceReq, Integer quorum) {

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
        accessControlMap = new HashMap<>();
        this.quorumSize = quorum;
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
        for (AccessRequest request : to.getListOfAccessGiven()) {
            AccessControl accessControl = accessControlMap.get(request.getTo());
            accessControl.removeAccess(request, to.getOwnerRelation());

            if (!accessControl.containsPermissions()) {
                accessControlMap.remove(request.getTo());
            }
        }
        for (Admins i : to.getDelegateTo()) {
            removeDelegation(i, ownerRelation);
        }
        to.removeOwner(ownerRelation);
        if (to.getOwnerRelation().size() == 0) {
            logger.info("User " + to + " is not an admin anymore");
            uidAdminMap.remove(to.getUid());
        } else {
            for (AccessRequest request : to.getListOfAccessGiven()) {
                giveAccess(request);
            }
        }
    }

    private void updateOwnersForChildren(Admins to, Map<Integer, Integer> ownerRelation) {
        for (AccessRequest request : to.getListOfAccessGiven()) {
            accessControlMap.get(request.getTo()).removeAccess(request, to.getOwnerRelation());
        }
        to.addOwner(ownerRelation);
        for (Admins i : to.getDelegateTo()) {
            updateOwnersForChildren(i, ownerRelation);
        }
        for (AccessRequest request : to.getListOfAccessGiven()) {
            accessControlMap.get(request.getTo()).addToAccessControl(request, to.getOwnerRelation());
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
        for (AccessControl accessControl : accessControlMap.values()) {
            accessControl.updateOwner(from, to);
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

    public StatusCode giveAccess(AccessRequest request) {
        if (uidAdminMap.containsKey(request.getFrom())) {
            Admins fromAdmin = uidAdminMap.get(request.getFrom());
            AccessControl accessControl = accessControlMap.getOrDefault(request.getTo(), new AccessControl());
            if (fromAdmin.alreadyInAccessList(request.getTo())) {
                accessControl.removeAccess(fromAdmin.getAccessRequestsGiven().get(request.getTo()), fromAdmin.getOwnerRelation());
            }
            fromAdmin.addToAccessList(request);
            accessControl.addToAccessControl(request, fromAdmin.getOwnerRelation());
            this.accessControlMap.put(request.getTo(), accessControl);
            return StatusCode.SUCCESS;
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }

    public StatusCode revokeAccess(AccessRequest request) {
        if (uidAdminMap.containsKey(request.getFrom())) {
            Admins fromAdmin = uidAdminMap.get(request.getFrom());
            if (!accessControlMap.containsKey(request.getTo())) {
                return StatusCode.ACCESS_NOT_GIVEN_BEFORE;
            }
            if (!fromAdmin.containsAccessRequest(request)) {
                return StatusCode.ACCESS_NOT_GIVEN_BEFORE;
            }

            AccessControl accessControl = accessControlMap.get(request.getTo());
            accessControl.removeAccess(request, fromAdmin.getOwnerRelation());
            if (!accessControl.containsPermissions()) {
                accessControlMap.remove(request.getTo());
            }
            fromAdmin.removeFromAccessList(request);
            return StatusCode.SUCCESS;
        } else {
            return StatusCode.NOT_AN_ADMIN;
        }
    }

    public boolean doesUserHaveSelectAccess(Integer uid) {
        if (uidAdminMap.containsKey(uid)) {
            return true;
        } else if (accessControlMap.containsKey(uid)) {
            AccessControl accessControl = accessControlMap.get(uid);
            return accessControl.selectOwnerSize() >= quorumSize;
        } else {
            return false;
        }
    }
    public boolean doesUserHaveInsertAccess(Integer uid) {
        if (uidAdminMap.containsKey(uid)) {
            return true;
        } else if (accessControlMap.containsKey(uid)) {
            AccessControl accessControl = accessControlMap.get(uid);
            return accessControl.insertOwnerSize() >= quorumSize;
        } else {
            return false;
        }
    }
    public boolean doesUserHaveDeleteAccess(Integer uid) {
        if (uidAdminMap.containsKey(uid)) {
            return true;
        } else if (accessControlMap.containsKey(uid)) {
            AccessControl accessControl = accessControlMap.get(uid);
            return accessControl.deleteOwnerSize() >= quorumSize;
        } else {
            return false;
        }
    }

    public boolean doesUserHaveUpdateAccess(Integer uid) {
        if (uidAdminMap.containsKey(uid)) {
            return true;
        } else if (accessControlMap.containsKey(uid)) {
            AccessControl accessControl = accessControlMap.get(uid);
            return accessControl.updateOwnerSize() >= quorumSize;
        } else {
            return false;
        }
    }
    public boolean doesUserHaveDropAccess(Integer uid) {
        if (uidAdminMap.containsKey(uid)) {
            return true;
        } else if (accessControlMap.containsKey(uid)) {
            AccessControl accessControl = accessControlMap.get(uid);
            return accessControl.dropOwnerSize() >= quorumSize;
        } else {
            return false;
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

    public String printAccess() {
        return accessControlMap.toString();
    }

    public String printAcceptance() {
        return stillToAcceptOwnership.toString();
    }
}