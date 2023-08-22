package net.lakis.cerebro.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.lakis.cerebro.lang.Hex;
import net.lakis.cerebro.lang.Strings;


public class Hash {


    /**
     * The MD5 message digest algorithm defined in RFC 1321.
     */
	private static final MessageDigest MD5 = getDigest("MD5");



	/**
     * Returns an MD5 MessageDigest.
     *
     * @return An MD5 digest instance.
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught, which should never happen because MD5 is a
     *             built-in algorithm
     * @see MessageDigestAlgorithms#MD5
     */
    public static MessageDigest getMd5Digest() {
    	return  MD5;
    }
    
    
    /**
     * Returns a {@code MessageDigest} for the given {@code algorithm}.
     *
     * @param algorithm
     *            the name of the algorithm requested. See <a
     *            href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA"
     *            >Appendix A in the Java Cryptography Architecture Reference Guide</a> for information about standard
     *            algorithm names.
     * @return A digest instance.
     * @see MessageDigest#getInstance(String)
     * @throws IllegalArgumentException
     *             when a {@link NoSuchAlgorithmException} is caught.
     */
    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    
    
    /**
     * Calculates the MD5 digest and returns the value as a 16 element {@code byte[]}.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest
     */
    public static byte[] md5(final byte[] data) {
        return MD5.digest(data);
    }

    
    /**
     * Calculates the MD5 digest and returns the value as a 16 element {@code byte[]}.
     *
     * @param data
     *            Data to digest; converted to bytes using {@link StringUtils#getBytesUtf8(String)}
     * @return MD5 digest
     */
    public static byte[] md5(final String data) {
        return md5(Strings.getBytesUtf8(data));
    }
    
    /**
     * Calculates the MD5 digest and returns the value as a 32 character hex string.
     *
     * @param data
     *            Data to digest
     * @return MD5 digest as a hex string
     */
    public static String md5Hex(final String data) {
        return Hex.getHexString(md5(data));
    }
}
