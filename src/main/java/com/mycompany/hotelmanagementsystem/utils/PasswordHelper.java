package com.mycompany.hotelmanagementsystem.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHelper {
    private static final int COST_FACTOR = 12;

    private PasswordHelper() {}

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(COST_FACTOR));
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
    public static void main(String[] args) {
        System.out.println(hash("1"));
    }
}
