package com.stashinvest.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashHelper {

	private static final String SHA_256_HASHING_ALGORITHM = "SHA-256";

	public static String getSHA256Hash(String input) {
		String hashedValue = "";
		byte[] hashedByte = null;
		try {
			MessageDigest digest = MessageDigest
					.getInstance(SHA_256_HASHING_ALGORITHM);
			hashedByte = digest.digest(input.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < hashedByte.length; i++) {
			hashedValue += Integer.toString((hashedByte[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return hashedValue;
	}

	public static String getSaltedPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
}
