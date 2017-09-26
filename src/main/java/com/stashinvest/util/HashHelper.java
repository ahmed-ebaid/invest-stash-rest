package com.stashinvest.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashHelper {
	private static final Logger log = Logger.getLogger(HashHelper.class);
	private static final String SHA_256_HASHING_ALGORITHM = "SHA-256";

	/**
	 * @param input
	 *            input to generate the SHA-256 hash for
	 * @return SHA-256 hashed value of the input.
	 */
	public static String getSHA256Hash(String input) {
		String hashedValue = "";
		byte[] hashedByte = null;
		try {
			MessageDigest digest = MessageDigest
					.getInstance(SHA_256_HASHING_ALGORITHM);
			hashedByte = digest.digest(input.getBytes());
		} catch (NoSuchAlgorithmException e) {
			log.error("Fail to generate hash for input: " + input, e);
		}
		for (int i = 0; i < hashedByte.length; i++) {
			hashedValue += Integer.toString((hashedByte[i] & 0xff) + 0x100, 16)
					.substring(1);
		}
		return hashedValue;
	}

	/**
	 * @param password
	 *            password to generated hash for
	 * @return hashed + salted password
	 */
	public static String getSaltedPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}
}
