package com.pain.apple.lab.codec;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String md5(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(digest.digest(text.getBytes(StandardCharsets.UTF_8)));
    }
}
