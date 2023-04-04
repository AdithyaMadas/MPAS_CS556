package com.madas.cs556.services.authorizationServices.constants;

public enum StatusCode {
    SUCCESS(200, "Success!"),
    NOT_AN_ADMIN(100, "User trying to delegate access is not an admin!"),
    ADMIN_ACCESS_NOT_ASSIGNED(101, "Trying to remove credentials for a User who doesn't have admin rights!"),
    DELEGATION_NOT_PROVIDED_BEFORE(102, "From User has not given delegation access to user before!"),

    IS_NOT_AN_OWNER(103, "From User is not a owner!"),
    MODE_NOT_SUPPORTED(104, "Mode not supported"),

    NO_ONE_ASSIGNED_OWNERSHIP(105, "No one assigned ownership!"),

    TRYING_TO_DELEGATE_ITSELF(106, "User trying to delegate themselves"),
    CREATES_CYCLE(107, "Creates a cyclic dependency"),
    ACCESS_NOT_GIVEN_BEFORE(107, "Access not given before"),
    ;

    private final int code;
    private final String description;

    private StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }

}
