package org.ati.core.service;

public interface SecurityService {
    boolean isAuthenticated();

    void autoLogin(String username, String password);
}
