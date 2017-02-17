package com.phloxinc.whereworks.controller.auth;

public interface AuthListener<T, E> {
    void onLoginSuccess(T result);
    void onLoginFailed(E error);
}
