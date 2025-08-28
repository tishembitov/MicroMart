package ru.tishembitov.userservice.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public abstract class HashUtil {
    public static String sha256Hash(String str) {
        return Hashing
                .sha256()
                .hashString(str, StandardCharsets.UTF_8)
                .toString();
    }
}
