package com.fitradar.frontend.shared.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class BasicAuthUtils {

    private BasicAuthUtils() {
    }

    public static String buildBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }
}