package com.as4mail.svev.enc;

import com.as4mail.svev.exception.EncRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import static java.security.KeyStore.getInstance;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Joze Rihtarsic <joze.rihtarsic@sodisce.si>
 */
public class SVEVCrypto {

    private static final String ENC_SIMETRIC_KEY_ALG = XMLCipher.RSA_OAEP;

    static {
        org.apache.xml.security.Init.init();
    }

    /**
     *
     */
    public SVEVCrypto() {
    }

    /**
     *
     * @param elKey
     * @param rsaKey
     * @param targetKeyAlg
     * @return
     * @throws SEDSecurityException
     */
    public Key decryptEncryptedKey(Element elKey, Key rsaKey, SymEncAlgorithms targetKeyAlg) {

        Key keyDec = null;

        XMLCipher keyCipher;
        try {
            keyCipher = XMLCipher.getInstance(ENC_SIMETRIC_KEY_ALG);
            keyCipher.init(XMLCipher.UNWRAP_MODE, null);
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("NoSuchAlgorithm: " + ENC_SIMETRIC_KEY_ALG, ex);
        }

        EncryptedKey key;
        try {
            key = keyCipher.loadEncryptedKey(elKey.getOwnerDocument(), elKey);
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("EncryptionException: " + ex.getMessage(), ex);
        }

        XMLCipher chDec;
        try {
            chDec = XMLCipher.getInstance();
            chDec.init(XMLCipher.UNWRAP_MODE, rsaKey);
            keyDec = chDec.decryptKey(key, targetKeyAlg.getURI());
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("EncryptionException: " + ex.getMessage(), ex);
        }
        return keyDec;
    }

    public KeyStore getKeystore(File filePath, String storeType, char[] password) {
        KeyStore keyStore = null;
        try {
            keyStore = getInstance(storeType);

        } catch (KeyStoreException ex) {
            throw new EncRuntimeException(String.format("KeyStoreException '%s' for store type %s", ex.getMessage(), storeType), ex);
        }

        try (FileInputStream fis = new FileInputStream(filePath)) {
            keyStore.load(fis, password);
        } catch (IOException ex) {
            throw new EncRuntimeException("ReadWriteFileException: " + ex.getMessage(), ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new EncRuntimeException("NoSuchAlgorithmException: " + ex.getMessage(), ex);
        } catch (CertificateException ex) {
            throw new EncRuntimeException("CertificateException: " + ex.getMessage(), ex);
        }

        return keyStore;
    }

    public Key getKeyForCertificate(KeyStore  keystore, X509Certificate cert) {
        String keystore.getCertificateAlias(cert);
        return null;
    }

    public Key decryptEncryptedKey(EncryptedKey key, Key rsaKey, SymEncAlgorithms targetKeyAlg) {

        Key keyDec = null;

        XMLCipher keyCipher;
        try {
            keyCipher = XMLCipher.getInstance(ENC_SIMETRIC_KEY_ALG);
            keyCipher.init(XMLCipher.UNWRAP_MODE, null);
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("NoSuchAlgorithm: " + ENC_SIMETRIC_KEY_ALG, ex);
        }

        XMLCipher chDec;
        try {
            chDec = XMLCipher.getInstance();
            chDec.init(XMLCipher.UNWRAP_MODE, rsaKey);
            keyDec = chDec.decryptKey(key, targetKeyAlg.getURI());
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("EncryptionException: " + ex.getMessage(), ex);
        }

        return keyDec;

    }

    /**
     * Method encrypts input stream to output stream with given key
     *
     * @param fIn input stream to encrypt
     * @param fOut - output stream with encrypted data
     * @param skey - secret key to encrypt stream
     */
    public void decryptFile(File fIn, File fOut, Key skey) {
        try (FileInputStream fis = new FileInputStream(fIn);
                FileOutputStream fos = new FileOutputStream(fOut)) {
            decryptStream(fis, fos, skey, Cipher.DECRYPT_MODE);
        } catch (IOException ex) {
            throw new EncRuntimeException("ReadWriteFileException: " + ex.getMessage(), ex);
        }
    }

    /**
     * Method decrypts input stream to output stream with given key
     *
     * @param is input encrypted stream
     * @param os - output decrypted stream
     * @param skey - secret key to decrypt stream
     */
    public void decryptStream(InputStream is, OutputStream os, Key skey) {
        decryptStream(is, os, skey, Cipher.DECRYPT_MODE);
    }

    public EncryptedKey file2EncryptedKey(File encKeyFile) {

        Document doc;
        try (InputStream fis = new FileInputStream(encKeyFile)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            doc = builder.parse(fis);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            throw new EncRuntimeException("ParserException: " + ex.getMessage(), ex);
        }
        return element2EncryptedKey(doc.getDocumentElement());

    }

    public EncryptedKey element2EncryptedKey(Element e) {

        XMLCipher keyCipher;
        try {
            keyCipher = XMLCipher.getInstance(ENC_SIMETRIC_KEY_ALG);
            keyCipher.init(XMLCipher.UNWRAP_MODE, null);
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("NoSuchAlgorithm: " + ENC_SIMETRIC_KEY_ALG, ex);
        }

        EncryptedKey key;
        try {
            key = keyCipher.loadEncryptedKey(e.getOwnerDocument(), e);
        } catch (XMLEncryptionException ex) {
            throw new EncRuntimeException("EncryptionException: " + ex.getMessage(), ex);
        }
        return key;
    }

    /**
     * Method encrypt decrypts input stream to output stream with given key
     *
     * @param is input encrypted stream
     * @param os - output decrypted stream
     * @param skey - secret key to decrypt stream
     * @param chiperMode - chiper mode: Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE
     * @throws SEDSecurityException
     */
    private void decryptStream(InputStream is, OutputStream os, Key skey, int chiperMode) {

        Cipher dcipher;
        try {
            dcipher = Cipher.getInstance(skey.getAlgorithm());
        } catch (NoSuchAlgorithmException ex) {
            throw new EncRuntimeException("NoSuchAlgorithmException: " + skey.getAlgorithm(), ex);
        } catch (NoSuchPaddingException ex) {
            throw new EncRuntimeException("NoSuchPaddingException: " + skey.getAlgorithm(), ex);
        }
        try {
            dcipher.init(chiperMode, skey);
        } catch (InvalidKeyException ex) {
            throw new EncRuntimeException("InvalidKeyException: " + ex.getMessage(), ex);
        }

        try (CipherOutputStream cos = new CipherOutputStream(os, dcipher)) {

            byte[] block = new byte[1024];
            int i;
            while ((i = is.read(block)) != -1) {
                cos.write(block, 0, i);
            }
        } catch (IOException ex) {
            throw new EncRuntimeException("ReadWriteFileException: " + ex.getMessage(), ex);
        }
    }

    /**
     *
     */
    public enum SymEncAlgorithms {

        /**
         *
         */
        AES128_CBC("http://www.w3.org/2001/04/xmlenc#aes128-cbc", "AES", 128),
        /**
         *
         */
        AES192_CBC("http://www.w3.org/2001/04/xmlenc#aes192-cbc", "AES", 192),
        /**
         *
         */
        AES256_CBC("http://www.w3.org/2001/04/xmlenc#aes256-cbc", "AES", 256);

        private final String mstrW3_uri;
        private final String mstrJce_name;
        private final int miKey_len;

        private SymEncAlgorithms(String uri, String name, int iKeyLen) {
            this.mstrW3_uri = uri;
            this.mstrJce_name = name;
            this.miKey_len = iKeyLen;
        }

        /**
         *
         * @return
         */
        public String getJCEName() {
            return mstrJce_name;
        }

        /**
         *
         * @return
         */
        public String getURI() {
            return mstrW3_uri;
        }

        /**
         *
         * @return
         */
        public int getKeyLength() {
            return miKey_len;
        }

        public static SymEncAlgorithms getAlgorithmByURI(String uri) {
            if (uri != null && !uri.isEmpty()) {
                for (SymEncAlgorithms sa : SymEncAlgorithms.values()) {
                    if (Objects.equals(uri, sa.getURI())) {
                        return sa;
                    }

                }

            }
            return null;
        }

    }

}
