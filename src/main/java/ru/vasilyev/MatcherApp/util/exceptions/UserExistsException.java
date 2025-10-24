package ru.vasilyev.MatcherApp.util.exceptions;

import javax.naming.AuthenticationException;

public class UserExistsException extends AuthenticationException {
    public UserExistsException(String message) {
        super(message);
    }
}
