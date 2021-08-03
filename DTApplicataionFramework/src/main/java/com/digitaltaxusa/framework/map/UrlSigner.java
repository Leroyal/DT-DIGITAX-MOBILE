package com.digitaltaxusa.framework.map;

import com.digitaltaxusa.framework.map.constants.ConfigurationManager;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UrlSigner {

    /**
     * The URL is a static which should already be URL-encoded. In practice,
     * you will likely have code which assembles your URL from user or web service input
     * and plugs those values into its parameters. This variable stores the binary key,
     * which is computed from the string (Base64) key.
     */
    private static byte[] key;

    /**
     * Constructor
     *
     * @param keyString The Google CryptoKey
     */
    public UrlSigner(String keyString) {
        // convert the key from 'web safe' base 64 to binary
        keyString = keyString.replace('-', '+');
        keyString = keyString.replace('_', '/');
        UrlSigner.key = Base64.decodeBase64(keyString);
    }

    /**
     * Method is used to sign google url.
     *
     * @param inputUrl The URL to sign.
     * @return The signed URL
     * @throws IOException              Signals a general, I/O-related error.
     * @throws InvalidKeyException      Indicates exceptional conditions, caused by an invalid key.
     * @throws NoSuchAlgorithmException Indicates that a requested algorithm could not be found.
     */
    public static String signURL(
            String inputUrl
    ) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        URL url = new URL(inputUrl);
        String googleCryptoKey = ConfigurationManager.GOOGLE_CRYPTO_KEY;
        UrlSigner signer = new UrlSigner(googleCryptoKey);
        String request = signer.signRequest(url.getPath(), url.getQuery());
        return url.getProtocol() + "://" + url.getHost() + request;
    }

    /**
     * Method is used to create signature.
     *
     * @param path  The path used to query the proper URL components to sign.
     * @param query The query string.
     * @return The signed request
     * @throws NoSuchAlgorithmException Indicates that a requested algorithm could not be found.
     * @throws InvalidKeyException      Indicates exceptional conditions, caused by an invalid key.
     */
    private String signRequest(
            String path,
            String query
    ) throws NoSuchAlgorithmException,
            InvalidKeyException {

        // retrieve the proper URL components to sign
        String resource = path + '?' + query;

        // get an HMAC-SHA1 signing key from the raw key bytes
        SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

        // get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);

        // compute the binary signature for the request
        byte[] sigBytes = mac.doFinal(resource.getBytes());

        // base 64 encode the binary signature
        String signature = Base64.encodeBase64String(sigBytes);

        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-');
        signature = signature.replace('/', '_');
        return resource + "&signature=" + signature;
    }
}