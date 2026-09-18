package com.freshprint.dto;

/** A quick receipt; the operation may still be running. */
public record DecisionReceipt(String operationId, String status) {
}
