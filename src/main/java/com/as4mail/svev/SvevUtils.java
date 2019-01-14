/*
 * Copyright 2018, Joze Rihtarsic
 * 
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except in
 * compliance with the Licence. You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package com.as4mail.svev;

import com.as4mail.svev.exception.SignRuntimeException;
import com.as4mail.svev.utils.FOPUtils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters.SignerPosition;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Joze Rihtarsic
 */
public class SvevUtils {

    FOPUtils mFopUtil = null;
    File mFopConfigFile;
    File mFopXsltFolder;

    public SvevUtils(File mFopConfigFile, File mFopXsltFolder) {
        this.mFopConfigFile = mFopConfigFile;
        this.mFopXsltFolder = mFopXsltFolder;
    }
    
    

    public void generateDeliveryAdviceVisualization(File soapMesage, File outVisualizationPDF,
            File recipientKeystore, String recipientKeystorePasswd, String recipientKeyAlias, String recipientKeyPasswd,
            File systemKeystore, String systemKeystorePasswd, String systemKeyAlias, String systemKeyPasswd) throws IOException {

        
        // generate vizualization file
        getFop().generateVisualization(soapMesage, outVisualizationPDF, "LegalDelivery_ZPP-AdviceOfDelivery.fo", "application/pdf");
        
        List<SignatureData> signDatas = new ArrayList<>();
        // sign file with visualization for final receiver
        JKSSignatureToken jksTokenRec = new JKSSignatureToken(recipientKeystore, recipientKeystorePasswd);
        KSPrivateKeyEntry signatureKeyRec = jksTokenRec.getKey(recipientKeyAlias, systemKeyPasswd);
        SignatureData sdRecipient = new SignatureData();
        sdRecipient.setReason("DeliverOfAdvice");
        sdRecipient.setJksToken(jksTokenRec);
        sdRecipient.setSignatureKey(signatureKeyRec);
        sdRecipient.setShowSignatureVisualization(true);
        signDatas.add(sdRecipient);
       
        // sign file without visualization for delivered sistem 
        JKSSignatureToken jksTokenIS = new JKSSignatureToken(recipientKeystore, recipientKeystorePasswd);
        KSPrivateKeyEntry signatureKeyIS = jksTokenRec.getKey(recipientKeyAlias, systemKeyPasswd);
        SignatureData sdSystem = new SignatureData();
        sdSystem.setReason("DeliverOfAdvice");
        sdSystem.setJksToken(jksTokenIS);
        sdSystem.setSignatureKey(signatureKeyIS);
        sdSystem.setShowSignatureVisualization(false);
        signDatas.add(sdSystem);
        // sig file
        sigPDFFileXMLSignature(outVisualizationPDF, signDatas);

    }
    
    public void sigPDFFileXMLSignature(File fileToSign,List<SignatureData> sigDatas) throws IOException {
        sigDatas.forEach(data -> {
            sigPDFFileXMLSignature(fileToSign, data);
        });
    }

    public void sigPDFFileXMLSignature(File fileToSign, SignatureData sigData)  {

        // -------------------------------
        // document to be signed
        FileDocument documentToSign = new FileDocument(fileToSign);
        // signature key 
        KeyStoreSignatureTokenConnection jksToken = sigData.getJksToken();

        DSSPrivateKeyEntry signatureKey = sigData.getSignatureKey();

        Date signDate = Calendar.getInstance().getTime();
        // -------------------------------
        // create signature parameters
        PAdESSignatureParameters signatureParameters = new PAdESSignatureParameters();
        signatureParameters.bLevel().setSigningDate(signDate);
        signatureParameters.setSigningCertificate(signatureKey.getCertificate());
        signatureParameters.setCertificateChain(signatureKey.getCertificateChain());
        signatureParameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);

        signatureParameters.setLocation(sigData.getLocation());
        signatureParameters.setReason(sigData.getReason());
        signatureParameters.setContactInfo(sigData.getContactInfo());

        if (sigData.showSignatureVisualization()) {
            SignatureImageParameters imageParameters = new SignatureImageParameters();

            // -------------------------------
            // set signature image
            imageParameters.setImage(new InMemoryDocument(SvevUtils.class.getResourceAsStream("/test-signature-image.jpg")));
            imageParameters.setPage(1);
            imageParameters.setxAxis(5);
            imageParameters.setyAxis(5);

            // -------------------------------
            // set signature text
            SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
            String name = signatureKey.getCertificate().getSubjectX500Principal().toString();
            textParameters.setText(String.format("Subject: %s\nIssuer: %s\nSerial: %s\nDate: %s",
                    name.substring(name.indexOf("CN")),
                    signatureKey.getCertificate().getIssuerX500Principal().toString(),
                    signatureKey.getCertificate().getSerialNumber().toString(),
                    (new SimpleDateFormat("dd. MM. yyyy HH:mm")).format(signDate)));
            textParameters.setTextColor(Color.BLACK);
            textParameters.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 6));
            textParameters.setSignerNamePosition(SignerPosition.LEFT);
            imageParameters.setTextParameters(textParameters);

            signatureParameters.setSignatureImageParameters(imageParameters);

        }

        // create signature service
        DocumentSignatureService<PAdESSignatureParameters> service = new PAdESService(new CommonCertificateVerifier());
        ToBeSigned dataToSign = service.getDataToSign(documentToSign, signatureParameters);

        SignatureValue signatureValue = jksToken.sign(dataToSign,
                signatureParameters.getDigestAlgorithm(),
                signatureKey);

        DSSDocument signedDocument = service.signDocument(documentToSign, signatureParameters, signatureValue);
        
        try {
            signedDocument.save(fileToSign.getAbsolutePath());
        } catch (IOException ex) {
            throw new SignRuntimeException("Error occured while saving signed document to file: " + fileToSign.getAbsolutePath(), ex);
        }

    }

    protected FOPUtils getFop() {
        if (mFopUtil == null) {
            mFopUtil = new FOPUtils(this.mFopConfigFile, this.mFopXsltFolder);
        }
        return mFopUtil;
    }

    public File getFopConfigFile() {
        return mFopConfigFile;
    }

    public void setFopConfigFile(File fopConfigFile) {
        this.mFopConfigFile = fopConfigFile;
    }

    public File getFopXsltFolder() {
        return mFopXsltFolder;
    }

    public void setFopXsltFolder(File fopXsltFolder) {
        this.mFopXsltFolder = fopXsltFolder;
    }

    /**
     * Signature data
     * 
     */
    public static class SignatureData {
        
        String reason;
        String contactInfo;
        String location;
        boolean showSignatureVisualization = false;
        
        KeyStoreSignatureTokenConnection jksToken;
        DSSPrivateKeyEntry signatureKey;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getContactInfo() {
            return contactInfo;
        }

        public void setContactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public boolean showSignatureVisualization() {
            return showSignatureVisualization;
        }

        public void setShowSignatureVisualization(boolean showSignatureVisualization) {
            this.showSignatureVisualization = showSignatureVisualization;
        }

       

        public KeyStoreSignatureTokenConnection getJksToken() {
            return jksToken;
        }

        public void setJksToken(KeyStoreSignatureTokenConnection jksToken) {
            this.jksToken = jksToken;
        }

        public DSSPrivateKeyEntry getSignatureKey() {
            return signatureKey;
        }

        public void setSignatureKey(DSSPrivateKeyEntry signatureKey) {
            this.signatureKey = signatureKey;
        }
    }
    

}
