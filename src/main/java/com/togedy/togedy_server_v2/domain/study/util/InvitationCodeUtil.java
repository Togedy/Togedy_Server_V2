package com.togedy.togedy_server_v2.domain.study.util;

import java.security.SecureRandom;

public class InvitationCodeUtil {

    private static final int INVITATION_CODE_LENGTH = 6;

    private InvitationCodeUtil() {
    }

    public static String generateInvitationCode() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(INVITATION_CODE_LENGTH);
        for (int i = 0; i < INVITATION_CODE_LENGTH; i++) {
            sb.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

}
