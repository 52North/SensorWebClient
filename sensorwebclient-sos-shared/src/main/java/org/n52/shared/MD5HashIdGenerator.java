package org.n52.shared;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashIdGenerator implements IdGenerator {
    
    private String prefix;

    public MD5HashIdGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String generate(String[] parameters) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (String parameter : parameters) {
                if (parameter != null) {
                    md.update(parameter.getBytes());
                }
            }
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            return prefix + bigInt.toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 message digester not available!", e);
        }
    }

}
