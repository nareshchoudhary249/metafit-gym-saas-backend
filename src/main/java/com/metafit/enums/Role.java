package com.metafit.enums;


/**
 * User role enumeration for role-based access control
 */
public enum Role {
    OWNER("Owner", "Full system access, can manage subscription and staff"),
    ADMIN("Admin", "Can manage all gym operations except billing"),
    RECEPTION("Reception", "Can manage members, attendance, and payments"),
    TRAINER("Trainer", "Can view assigned members and add progress notes"),
    ACCOUNTANT("Accountant", "Can view and manage payments and reports");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasFullAccess() {
        return this == OWNER;
    }

    public boolean canManageStaff() {
        return this == OWNER || this == ADMIN;
    }

    public boolean canManageMembers() {
        return this == OWNER || this == ADMIN || this == RECEPTION;
    }

    public boolean canManagePayments() {
        return this == OWNER || this == ADMIN || this == RECEPTION || this == ACCOUNTANT;
    }

    public boolean canViewReports() {
        return this != TRAINER;
    }
}
