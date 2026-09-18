package com.freshprint.dto;

/** The exact versions a user reviewed before making a decision. */
public record DecisionRequest(String decision, int expectedBaselineVersion, int targetVersion) {
}
