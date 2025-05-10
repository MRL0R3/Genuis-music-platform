package com.genius.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for securely hashing and verifying passwords using PBKDF2 with HMAC-SHA256.
 * This implementation follows security best practices for password storage.
 */
public class PasswordHasher {
    // Security parameters - can be adjusted based on performance/security needs
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256; // bits
    private static final int SALT_LENGTH = 16; // bytes
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    
    // Delimiter for separating salt and hash in the stored string
    private static final String DELIMITER = ":";

    /**
     * Hashes a password with a randomly generated salt.
     * @param password The plaintext password to hash
     * @return A string containing the salt and hash separated by DELIMITER
     * @throws IllegalStateException if the hashing fails
     */
    public static String hash(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password
            byte[] hash = pbkdf2(password.toCharArray(), salt);
            
            // Combine salt and hash with delimiter
            return Base64.getEncoder().encodeToString(salt) + DELIMITER + 
                   Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash.
     * @param password The plaintext password to verify
     * @param storedHash The stored hash (salt + hash)
     * @return true if the password matches, false otherwise
     * @throws IllegalStateException if the verification fails
     */
    public static boolean verify(String password, String storedHash) {
        try {

            // Split the stored hash into salt and hash components
            String[] parts = storedHash.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the provided password with the same salt
            byte[] actualHash = pbkdf2(password.toCharArray(), salt);
            
            // Compare the hashes in constant time to prevent timing attacks
            return constantTimeEquals(actualHash, expectedHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            throw new IllegalStateException("Password verification failed", e);
        }
    }
    
    /**
     * Computes the PBKDF2 hash of a password with a given salt.
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
                password, 
                salt, 
                ITERATIONS, 
                KEY_LENGTH
        );
        
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }
    
    /**
     * Compares two byte arrays in constant time to prevent timing attacks.
     * @param a First byte array
     * @param b Second byte array
     * @return true if arrays are equal, false otherwise
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}