package com.fram.codingassignment.mvp.base.usecase;

/**
 * Created by thaile on 3/15/17.
 */

public class AuthorizationRequest implements RequestValues {

    private final String authorization;

    public AuthorizationRequest(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }
}
