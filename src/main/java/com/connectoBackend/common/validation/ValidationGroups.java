package com.connectoBackend.common.validation;

/**
 * Bean Validation groups.
 */
public final class ValidationGroups {

    // -> Prevent instantiation
    private ValidationGroups() {
    }

    // -> Validation group for create operations
    public interface Create {
    }

    // -> Validation group for update operations
    public interface Update {
    }
}