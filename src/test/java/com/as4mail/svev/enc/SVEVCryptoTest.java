/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.as4mail.svev.enc;

import com.as4mail.svev.TestData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Element;

/**
 *
 * @author vsrs
 */
public class SVEVCryptoTest {
    
    public SVEVCryptoTest() {
    }

    @Test
    public void testDecryptKey() throws IOException, KeyResolverException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        SVEVCrypto instance = new SVEVCrypto();
        // get ecrypted key from element
        EncryptedKey encKey = instance.file2EncryptedKey(TestData.S_ENC_KEY);
        
        assertNotNull(encKey);
        X509Certificate crt  = encKey.getKeyInfo().getX509Certificate();
        assertNotNull(crt);
        
        KeyStore keystore = instance.getKeystore(TestData.S_RECIPIENT_JKS, "JKS", TestData.S_RECIPIENT_JKS_PASSWD.toCharArray());
        String certAlias = keystore.getCertificateAlias(crt);
        
        // get key to decrypt symetric key
        Key certKey = keystore.getKey(certAlias, TestData.S_RECIPIENT_KEY_PASSWD.toCharArray());
        // get symetric key
        Key simetricKey = instance.decryptEncryptedKey(encKey, certKey, SVEVCrypto.SymEncAlgorithms.AES256_CBC);
        assertNotNull(simetricKey);
        // decrypt file
        File decFile = new File(TestData.S_OUT_FOLDER,"decryptedFile.txt");
        instance.decryptFile(TestData.S_ENC_PAYLOAD, decFile, simetricKey);
        // test content
        String content = new String(Files.readAllBytes(decFile.toPath()));
        assertEquals("This is test message", content);
        
    }
    
}
