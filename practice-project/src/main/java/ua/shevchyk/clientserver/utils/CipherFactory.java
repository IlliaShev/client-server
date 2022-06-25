package ua.shevchyk.clientserver.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import static ua.shevchyk.clientserver.utils.Constants.SECRET_KEY;


public class CipherFactory {

    private static Cipher cipher;
    private static final Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");


    private CipherFactory() {
    }

    private static Cipher init(int mode) throws Exception {
        byte[] bytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        Key key = new SecretKeySpec(bytes, "AES");
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, key, new IvParameterSpec(bytes));
        return cipher;
    }

    public static Cipher getEncryptCipher() throws Exception {
        return init(Cipher.ENCRYPT_MODE);
    }

    public static Cipher getDecryptCipher() throws Exception {
        return init(Cipher.DECRYPT_MODE);
    }


}
