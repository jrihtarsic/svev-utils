/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.as4mail.svev;

import java.io.File;
import java.nio.file.Paths;

/**
 *
 * @author vsrs
 */
public class TestData {
    public static final File S_OUT_FOLDER = new File("target");
    public static final File S_MESSAGES_FOLDER = Paths.get("src", "test", "resources", "soap-messages").toFile();
    public static final File S_RESOURCES_FOLDER = Paths.get("src", "test", "resources").toFile();
    public static final File S_FOP_INIT_FILE = Paths.get("src", "main", "resources", "fop", "fop.xconf").toFile();
    public static final File S_FOP_XSLT_FOLDER = Paths.get("src", "main", "resources", "fop", "xslt").toFile();
    
    public static final File S_RECIPIENT_JKS = new File(S_RESOURCES_FOLDER, "recipient-keystore.jks");
    public static final String S_RECIPIENT_JKS_PASSWD = "test1234";
    public static final String S_RECIPIENT_KEY_ALIAS = "recipient";
    public static final String S_RECIPIENT_KEY_PASSWD = "test1234";
    public static final File S_MSH_JKS = new File(S_RESOURCES_FOLDER, "msh-keystore.jks");
    public static final String S_MSH_JKS_PASSWD = "test1234";
    public static final String S_MSH_KEY_ALIAS = "msh";
    public static final String S_MSH_KEY_PASSWD = "test1234";
    
    public static final File S_ENC_KEY = Paths.get("src", "test", "resources", "test-files", "enc-key-test1.xml").toFile();
    public static final File S_ENC_PAYLOAD = Paths.get("src", "test", "resources", "test-files", "payload-test1.enc").toFile();
}
