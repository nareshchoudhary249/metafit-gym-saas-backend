package com.metafit.enums;

import lombok.Getter;

/**
 * Payment method enumeration
 */
@Getter
public enum PaymentMethod {
    CASH("Cash", "Cash payment", false),
    UPI("UPI", "Unified Payments Interface (PhonePe, GPay, Paytm)", true),
    CARD("Card", "Debit/Credit card payment", true),
    NET_BANKING("Net Banking", "Online bank transfer", true),
    CHEQUE("Cheque", "Bank cheque", false),
    WALLET("Digital Wallet", "Paytm, PhonePe wallet", true);

    private final String displayName;
    private final String description;
    private final boolean requiresTransactionId;

    PaymentMethod(String displayName, String description, boolean requiresTransactionId) {
        this.displayName = displayName;
        this.description = description;
        this.requiresTransactionId = requiresTransactionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresTransactionId() {
        return requiresTransactionId;
    }

    public boolean isDigital() {
        return this != CASH && this != CHEQUE;
    }
}
