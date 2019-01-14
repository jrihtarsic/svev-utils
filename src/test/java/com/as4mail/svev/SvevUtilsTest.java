/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.as4mail.svev;

import com.as4mail.svev.utils.FOPUtils;
import java.io.File;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vsrs
 */
public class SvevUtilsTest {
    
    
    SvevUtils testInstance = new SvevUtils(TestData.S_FOP_INIT_FILE, TestData.S_FOP_XSLT_FOLDER);
    
    public SvevUtilsTest() {
    }

    @Test
    public void testGenerateDeliveryAdviceVisualization() throws Exception {
        System.out.println("generateDeliveryAdviceVisualization");
        File soapMesage = new File(TestData.S_MESSAGES_FOLDER, "LegalDelivery_ZPP-DeliveryNotification.xml");
        File outVisualizationPDF = File.createTempFile("delivery-advice-", ".pdf", TestData.S_OUT_FOLDER);
       
        
        testInstance.generateDeliveryAdviceVisualization(soapMesage, outVisualizationPDF,
                TestData.S_RECIPIENT_JKS, TestData.S_RECIPIENT_JKS_PASSWD, 
                TestData.S_RECIPIENT_KEY_ALIAS, TestData.S_RECIPIENT_KEY_PASSWD, 
                TestData.S_MSH_JKS, TestData.S_MSH_JKS_PASSWD, 
                TestData.S_MSH_KEY_ALIAS, TestData.S_MSH_KEY_PASSWD);

    }

   
}
