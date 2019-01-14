package com.as4mail.svev.utils;

import com.as4mail.svev.exception.FOPRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Purpose of class is to generate PDF visualization needed in court mail
 * delivery process for SVEV 2.1-A. PDF visualizations are generated from
 * received ebMS 3.0 message (court notification mail with encrypted payloads)
 * soap message part.
 *
 */
public class FOPUtils {

    final static Logger LOG = Logger.getLogger(FOPUtils.class);

    private static final String FOP_CONF_FILE = "fop.xconf";

    String mTransformationFolder;
    FopFactory mfopFactory = null;
    File msfConfigFile;

    /**
     * Fop construct FOP utils with apache fop property file and folder path
     * with fop transformation
     *
     * @param configfile -
     * @param xsltFolder
     */
    public FOPUtils(File configfile, File xsltFolder) {
        msfConfigFile = configfile;
        mTransformationFolder = xsltFolder.getAbsolutePath();
        try {
            initFopFactory();
        } catch (SAXException | IOException ex) {
            String msg = "Error occured while initialize fop factory! Msg; "
                    + ex.getMessage();
            throw new FOPRuntimeException(msg, ex);
        }
    }

    /**
     * Method generates PDF visualization using SOAP envelope
     *
     * @param inSoapMessage Soap message
     * @param outVisualizationFile output PDF visualization
     * @param xsltName - FOPT transformation
     * @param mime - MIME type of visualization
     * @throws FOPRuntimeException
     */
    public void generateVisualization(File inSoapMessage, File outVisualizationFile, String xsltName, String mime) {

        try (FileOutputStream fos = new FileOutputStream(outVisualizationFile);
                InputStream isXSLT = getTransformationFile(xsltName);
                InputStream isSoapMsg = new FileInputStream(inSoapMessage)) {

            StreamSource ssXslt = new StreamSource(isXSLT);
            StreamSource ssSoap = new StreamSource(isSoapMsg);

            generateVisualization(ssSoap, fos, ssXslt, mime);

        } catch (IOException ex) {
            String msg = "Error generating visualization:" + ex.getMessage();
            throw new FOPRuntimeException(msg, ex);
        }
    }

    /**
     *
     * @param src
     * @param out
     * @param xslt
     * @param mime
     * @throws FOPException
     */
    public void generateVisualization(Source src, OutputStream out, Source xslt, String mime)
            throws FOPRuntimeException {

        try {
            Fop fop = getFopFactory().newFop(mime, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = factory.newTemplates(xslt);
            Transformer transformer = template.newTransformer();
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        } catch (IOException | SAXException | TransformerException ex) {
            String msg = "Error generating visualization" + ex.getMessage();
            throw new FOPRuntimeException(msg, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ignore) {

            }
        }
    }

    private FopFactory getFopFactory()
            throws SAXException, IOException {
        if (mfopFactory == null) {
            initFopFactory();
        }
        return mfopFactory;
    }

    private void initFopFactory() throws SAXException, IOException {
        if (mfopFactory == null) {
            mfopFactory = FopFactory.newInstance(msfConfigFile);

        }
    }

    /**
     * Return FOP transformation. If mTransformationFolder is null than returns
     * example transformation from class path
     *
     * @param xsltName - transformation name
     * @return
     */
    private InputStream getTransformationFile(String xsltName) throws FileNotFoundException {
        return new FileInputStream(new File(mTransformationFolder, xsltName));
    }
}
