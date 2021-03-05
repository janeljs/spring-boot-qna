package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public class HttpSessionUtils {
    public static final String USER_SESSION_KEY = "sessionUser";

    public static boolean isLoginUser(HttpSession session) {
        if (getSessionUser(session)==null) {
            return false;
        }
        return true;
    }

    public static User getSessionUser(HttpSession session) {
        return (User) session.getAttribute(USER_SESSION_KEY);
    }
}
