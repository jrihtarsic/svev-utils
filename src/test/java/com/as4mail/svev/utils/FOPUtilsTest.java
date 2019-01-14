/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.as4mail.svev.utils;

import com.as4mail.svev.TestData;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import javax.xml.transform.Source;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vsrs
 */
public class FOPUtilsTest {
    
   

    
    private final FOPUtils testInstance = new FOPUtils(TestData.S_FOP_INIT_FILE, TestData.S_FOP_XSLT_FOLDER);
    
    
 

    @Test
    public void testGenerateVisualization_fileInput() throws IOException {

        File inFileSoap = new File(TestData.S_MESSAGES_FOLDER, "LegalDelivery_ZPP-DeliveryNotification.xml");
        File fout = File.createTempFile("visualization-",".pdf", TestData.S_OUT_FOLDER);
        String xsltName = "LegalDelivery_ZPP-AdviceOfDelivery.fo";
        String mime = "application/pdf";
       
        testInstance.generateVisualization(inFileSoap, fout, xsltName, mime);
       
        assertTrue(inFileSoap.exists());
    }

    
}
