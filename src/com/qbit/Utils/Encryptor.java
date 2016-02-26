/*
 * Encryptor.java
 *
 * Created on December 5, 2002, 10:54 AM
 */

package com.qbit.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeUtility;
import java.io.*;


/**
 * User: cbates
 */
public final class Encryptor {
    private byte[] seed = {(byte)0x34,(byte)0x53,(byte)0xfd,(byte)0xed,(byte)0x43,(byte)0xf8,
                            (byte)0xff,(byte)0xf0,(byte)0xf5,(byte)0xf2,(byte)0x28,
                            (byte)0xffffffc8,(byte)0x23,(byte)0xffffffad,(byte)0x43,(byte)0x7c,(byte)0xb,(byte)0x51,
                            (byte)0xfffffff7,(byte)0xffffff85,(byte)0xffffffdf,(byte)0xffffffae,(byte)0xffffffa7,
                            (byte)0x75,(byte)0x76,(byte)0xb,(byte)0x49,(byte)0xffffffc8,(byte)0x23,(byte)0xffffffad,
                            (byte)0x43,(byte)0x7c,(byte)0xb,(byte)0x51,(byte)0xfffffff7,(byte)0x36,(byte)0xf3,(byte)0xc8,
                            (byte)0x45,(byte)0x54,(byte)0x65,(byte)0x53,(byte)0x43,(byte)0xc2,};

    private Cipher cipher = null;
    private SecretKey desedeKey = null;
    private static Encryptor encryptor = null;
    private static int i = 0;
    private static int j = 0;

    public static synchronized Encryptor getInstance(int i, int j) {
        if (encryptor == null) {
            Encryptor.i = i;
            Encryptor.j = j;
            encryptor = new Encryptor(i, j);
        }
        if (Encryptor.i != i || Encryptor.j != j) {
            return null;
        }

        return encryptor;
    }

    private Encryptor(int i, int j) {
        try {
            cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            desedeKey = new SecretKeySpec(seed, j, i, "DESede");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String pad(String number) {
        int count = 0;
        int end = number.length();
        for (int i = number.length(); i-- > 0 && count < 4; --end) {
            if (Character.isDigit(number.charAt(i))) {
                count++;
            }
        }
        if (count < 4) {
            return number;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < end; i++) {
            sb.append("* ");
        }

        return sb.append(number.substring(end)).toString();
    }

    public String decryptAndPad(String input) {
        return pad(decrypt(input));
    }

    public String decrypt(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, desedeKey);
            return new String(cipher.doFinal(decodeBytes(input.getBytes(), cipher)));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public String encrypt(String input) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, desedeKey);
            return new String(encodeBytes(cipher.doFinal(input.getBytes())));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encodeBytes(byte[] bytes) throws IOException {
        return encodeBytes(bytes, "base64");
    }

    public static byte[] encodeBytes(byte[] bytes, String encodingMethod) throws IOException {
        if (bytes == null) {
            return null;
        }

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(MimeUtility.encode(byteOut, encodingMethod))) {
            out.write(bytes);
            out.flush();
            return byteOut.toByteArray();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public static byte[] decodeBytes(byte[] bytes) throws IOException {
        return decodeBytes(bytes, null);
    }

    public static byte[] decodeBytes(byte[] bytes, Cipher cipher) throws IOException {
        if (bytes == null) {
            return null;
        }

        if (bytes.length == 0) {
            return new byte[0];
        }

        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
             InputStream decodedIn = MimeUtility.decode(bytesIn, "base64");
             DataInputStream in = new DataInputStream(decodedIn);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] tempArray = new byte[1000];
            while (true) {
                int count = in.read(tempArray);
                if (count < 0) {
                    break;
                }

                baos.write(tempArray, 0, count);
            }
            if (cipher != null) {
                cipher.doFinal(baos.toByteArray());
            }
            return baos.toByteArray();
        } catch (Exception e) {// Since we did it weird originally, to include encryption, we have to try to decrypt it the old way as well.
            try (InputStream decodedIn = MimeUtility.decode(new ByteArrayInputStream(bytes), "base64"); DataInputStream in = new DataInputStream(decodedIn)) {
                int length = in.readInt();
                byte[] result = new byte[length];
                in.readFully(result);
                return result;
            } catch (Exception | OutOfMemoryError exc) {// The old way and new didn't work, so we now we know we have a real problem.
                throw new IOException(exc.getMessage(), e);
            }
        }
    }
}
