package ru.infoenergo.android.common.cripto;

import android.annotation.TargetApi;
import android.os.Build;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import java.io.*;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptUtil {
    protected static final String ALGORITHM = "RSA";

    public RSAEncryptUtil(String key_dir) throws NoSuchAlgorithmException {
        try {
            init();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KEY_DIR = key_dir;
    }

    /**
     * Init java security to add BouncyCastle as an RSA provider
     */
    public static void init() throws NoSuchAlgorithmException {
        Security.addProvider(SecretKeyFactory.getInstance("PBEWithSHA256And256BitAES-CBC-BC").getProvider());
    }

    public String KEY_DIR = "";

    public String getPRIVATE_KEY_FILE() {
        return KEY_DIR + "_private.key";
    }

    public String PUBLIC_SERVER_KEY_FILE_NAME = "_public.key";

    public String getPUBLIC_SERVER_KEY_FILE() {
        return KEY_DIR + PUBLIC_SERVER_KEY_FILE_NAME;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void save_private_key_file(PrivateKey key) {
        File prv_key = new File(getPRIVATE_KEY_FILE());
        if (prv_key.exists())
            prv_key.delete();
        String string_key = RSAEncryptUtil.getKeyAsString(key);
        try (PrintStream out = new PrintStream(new FileOutputStream(getPRIVATE_KEY_FILE()))) {
            out.print(string_key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void save_public_key_file(PublicKey key) {
        File pub_key = new File(getPUBLIC_SERVER_KEY_FILE());
        if (pub_key.exists())
            pub_key.delete();
        String string_key = RSAEncryptUtil.getKeyAsString(key);
        try (PrintStream out = new PrintStream(new FileOutputStream(getPUBLIC_SERVER_KEY_FILE()))) {
            out.print(string_key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public PrivateKey restore_private_key_file() throws Exception {
        File key_file = new File(getPRIVATE_KEY_FILE());
        FileReader reader = null;
        String string_key = null;
        try {
            reader = new FileReader(key_file);
            char[] chars = new char[(int) key_file.length()];
            reader.read(chars);
            string_key = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        PrivateKey key = null;
        try {
            key = RSAEncryptUtil.getPrivateKeyFromString(string_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    public PublicKey restore_public_key_file() throws Exception {
        File key_file = new File(getPUBLIC_SERVER_KEY_FILE());
        FileReader reader = null;
        String string_key = null;
        try {
            reader = new FileReader(key_file);
            char[] chars = new char[(int) key_file.length()];
            reader.read(chars);
            string_key = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        PublicKey key = null;
        try {
            key = RSAEncryptUtil.getPublicKeyFromString(string_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Generate key which contains a pair of privae and public key using 1024 bytes
     *
     * @return key pair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();

        return key;
    }


    /**
     * Encrypt a text using public key.
     *
     * @param text The original unencrypted text
     * @param key  The public key
     * @return Encrypted text
     * @throws Exception
     */
    public static byte[] encrypt(byte[] text, PublicKey key) throws Exception {
        byte[] cipherText = null;
        //
        // get an RSA cipher object and print the provider
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        // encrypt the plaintext using the public key
        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipherText = cipher.doFinal(text);
        return cipherText;
    }

    /**
     * Encrypt a text using public key. The result is enctypted BASE64 encoded text
     *
     * @param text The original unencrypted text
     * @param key  The public key
     * @return Encrypted text encoded as BASE64
     * @throws Exception
     */
    public static String encrypt(String text, PublicKey key) throws Exception {
        String encryptedText;
        byte[] cipherText = encrypt(text.getBytes("UTF8"), key);
        encryptedText = encodeBASE64(cipherText);
        return encryptedText;
    }

    /**
     * Decrypt text using private key
     *
     * @param text The encrypted text
     * @param key  The private key
     * @return The unencrypted text
     * @throws Exception
     */
    public static byte[] decrypt(byte[] text, PrivateKey key) throws Exception {



        byte[] dectyptedText = null;
        // decrypt the text using the private key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        dectyptedText = cipher.doFinal(text);
        return dectyptedText;

    }

    /**
     * Decrypt BASE64 encoded text using private key
     *
     * @param text The encrypted text, encoded as BASE64
     * @param key  The private key
     * @return The unencrypted text encoded as UTF8
     * @throws Exception
     */
    public static String decrypt(String text, PrivateKey key) throws Exception {
        String result;
        // decrypt the text using the private key
        byte[] dectyptedText = decrypt(decodeBASE64(text), key);
        result = new String(dectyptedText, "UTF8");
        return result;

    }

    /**
     * Convert a Key to string encoded as BASE64
     *
     * @param key The key (private or public)
     * @return A string representation of the key
     */
    public static String getKeyAsString(Key key) {
        // Get the bytes of the key
        byte[] keyBytes = key.getEncoded();
        return encodeBASE64(keyBytes);
    }

    /**
     * Generates Private Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodeBASE64(key));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    /**
     * Generates Public Key from BASE64 encoded string
     *
     * @param key BASE64 encoded string which represents the key
     * @return The PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decodeBASE64(key));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    /**
     * Encode bytes array to BASE64 string
     *
     * @param bytes
     * @return Encoded string
     */
    public static String encodeBASE64(byte[] bytes) {
        //BASE64Encoder b64 = new BASE64Encoder();
        // return b64.encode(bytes, false);
        return new String(Base64.encodeBase64(bytes, false));
    }

    /**
     * Decode BASE64 encoded string to bytes array
     *
     * @param text The string
     * @return Bytes array
     * @throws IOException
     */
    public static byte[] decodeBASE64(String text) throws IOException {
        // BASE64Decoder b64 = new BASE64Decoder();
        // return b64.decodeBuffer(text);
        return Base64.decodeBase64(text.getBytes());
    }

    /**
     * Encrypt file using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @throws Exception
     */
    public static void encryptFile(String srcFileName, String destFileName, PublicKey key) throws Exception {
        encryptDecryptFile(srcFileName, destFileName, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * Decrypt file using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @throws Exception
     */
    public static void decryptFile(String srcFileName, String destFileName, PrivateKey key) throws Exception {
        encryptDecryptFile(srcFileName, destFileName, key, Cipher.DECRYPT_MODE);
    }

    /**
     * Encrypt and Decrypt files using 1024 RSA encryption
     *
     * @param srcFileName  Source file name
     * @param destFileName Destination file name
     * @param key          The key. For encryption this is the Private Key and for decryption this is the public key
     * @param cipherMode   Cipher Mode
     * @throws Exception
     */
    public static void encryptDecryptFile(String srcFileName, String destFileName, Key key, int cipherMode) throws Exception {
        OutputStream outputWriter = null;
        InputStream inputReader = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            String textLine = null;
            //RSA encryption data size limitations are slightly less than the key modulus size,
            //depending on the actual padding scheme used (e.g. with 1024 bit (128 byte) RSA key,
            //the size limit is 117 bytes for PKCS#1 v 1.5 padding. (http://www.jensign.com/JavaScience/dotnet/RSAEncrypt/)
            byte[] buf = cipherMode == Cipher.ENCRYPT_MODE ? new byte[100] : new byte[128];
            int bufl;
            // init the Cipher object for Encryption...
            cipher.init(cipherMode, key);

            // start FileIO
            outputWriter = new FileOutputStream(destFileName);
            inputReader = new FileInputStream(srcFileName);
            while ((bufl = inputReader.read(buf)) != -1) {
                byte[] encText = null;
                if (cipherMode == Cipher.ENCRYPT_MODE) {
                    encText = encrypt(copyBytes(buf, bufl), (PublicKey) key);
                } else {
                    encText = decrypt(copyBytes(buf, bufl), (PrivateKey) key);
                }
                outputWriter.write(encText);
            }
            outputWriter.flush();

        } finally {
            try {
                if (outputWriter != null) {
                    outputWriter.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (Exception e) {
                // do nothing...
            } // end of inner try, catch (Exception)...
        }
    }

    public static byte[] copyBytes(byte[] arr, int length) {
        byte[] newArr = null;
        if (arr.length == length) {
            newArr = arr;
        } else {
            newArr = new byte[length];
            for (int i = 0; i < length; i++) {
                newArr[i] = (byte) arr[i];
            }
        }
        return newArr;
    }
}

