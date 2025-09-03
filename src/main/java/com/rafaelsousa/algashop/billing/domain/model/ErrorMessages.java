package com.rafaelsousa.algashop.billing.domain.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessages {
    public static final String ERROR_INVOICE_ORDER_ID_CANNOT_BE_EMPTY = "Order ID cannot be empty";
    public static final String ERROR_INVOICE_MUST_HAVE_AT_LEAST_ONE_ITEM = "Invoice must have at least one item";
    public static final String ERROR_INVOICE_CANNOT_BE_MARKED_AS_PAID = "Invoice %s with status %s cannot be marked as paid";
    public static final String ERROR_INVOICE_IS_ALREADY_CANCELED = "Invoice %s is already canceled";
    public static final String ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE = "Invoice %s with status %s cannot be assigned a payment gateway code";
    public static final String ERROR_INVOICE_HAS_NO_PAYMENT_SETTINGS = "Invoice %s has no payment settigns";

    public static final String ERROR_CREDIT_CARD_LAST_NUMBERS_CANNOT_BE_EMPTY = "Last numbers cannot be empty";
    public static final String ERROR_CREDIT_CARD_BRAND_CANNOT_BE_EMPTY = "Brand cannot be empty";
    public static final String ERROR_CREDIT_CARD_INVALID_EXPIRATION_MONTH = "Invalid expiration month";
    public static final String ERROR_CREDIT_CARD_INVALID_EXPIRATION_YEAR = "Invalid expiration year";
    public static final String ERROR_CREDIT_CARD_GATEWAY_CODE_CANNOT_BE_EMPTY = "Gateway code cannot be empty";

    public static final String ERROR_LINE_ITEM_NUMBER_MUST_BE_GREATER_THAN_ZERO = "Number must be greater than zero";
    public static final String ERROR_LINE_ITEM_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "Amount must be greater than zero";
    public static final String ERROR_PAYMENT_SETTINGS_GATEWAY_CODE_CANNOT_BE_EMPTY = "Gateway code cannot be empty";
    public static final String ERROR_PAYMENT_SETTINGS_GATEWAY_CODE_ALREADY_ASSIGNED = "Gateway code already assigned";
    public static final String ERROR_INVOICE_ALREADY_EXISTS_FOR_ORDER = "Invoice already exists for order %s";
    public static final String ERROR_CREDIT_CARD_NOT_FOUND = "Credit card %s not found";
}