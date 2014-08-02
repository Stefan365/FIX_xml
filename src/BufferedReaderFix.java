/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * @author veres
 */
public class BufferedReaderFix {

    //reading file line by line in Java using BufferedReader       
    public FileInputStream rea = null;
    public BufferedReader reader = null;
    public BufferedWriter output = null;
    public FileOutputStream out = null;

    public void run() {

    
        //preparing of Map dictionaries:
        Map<String, String> fMap = null;
        Map<String, String> mMap = null;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MyXMLHandler myHandler = new MyXMLHandler();
            saxParser.parse("FIX42.xml", myHandler);
            fMap = myHandler.getFieldMap();
            mMap = myHandler.getMessageMap();
        } catch (IOException | ParserConfigurationException | SAXException e) {
        }

        //working with files:
        BufferedReaderFix ex;
        try {
            ex = new BufferedReaderFix();
            //do the text transformation:
            ex.doTransformation(fMap, mMap);
            
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(BufferedReaderFix.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(BufferedReaderFix.class.getName()).log(Level.SEVERE, null, ex1);
        }

    }
    //1.
    /**
     * Opens input file
     *
     * @param file
     * @throws java.io.FileNotFoundException
     */
    public void openFile(String file) throws FileNotFoundException {
        rea = new FileInputStream(file);
        reader = new BufferedReader(new InputStreamReader(rea));
    }

    public int changeToInt(String str) throws NumberFormatException {
        int i;
        i = Integer.parseInt(str);
        return i;
    }

    //2.
    /**
     * Do the transformation
     *
     * @param fieldMap
     * @param messageMap
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     */
    public void doTransformation(Map<String, String> fieldMap, Map<String, String> messageMap) throws UnsupportedEncodingException,
        FileNotFoundException, IOException {

        this.openFile("FIX.4.2-PELYNCH-CLIENT.messages.log");
        this.openFileWrite("transformed.log");
        
        String lineFin, line = reader.readLine();
            
        while (line != null) {
            lineFin = revert1Line(line, fieldMap, messageMap);
            output.write(lineFin);
            line = reader.readLine();
        }
        this.closeReadFile();
        this.closeWriteFile();
    }

    //3.
    /**
     * closes input file.
     *
     * @throws java.io.IOException
     */
    public void closeReadFile() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }

    //4.
    /**
     * Opens output file.
     *
     * @param file
     * @throws java.io.FileNotFoundException
     */
    public void openFileWrite(String file) throws FileNotFoundException {
        out = new FileOutputStream(file);
        output = new BufferedWriter(new OutputStreamWriter(out));
    }

    //5.
    /**
     * closes output file.
     *
     * @throws java.io.IOException
     */
    public void closeWriteFile() throws IOException {
        if (output != null) {
            output.close();
        }
    }

    //6.
    /**
     * Revert 1 line of source file into 1 output of log file.
     *
     * @param line
     * @param fieldMap
     * @param messageMap
     * @return
     */
    public String revert1Line(String line, Map<String, String> fieldMap, Map<String, String> messageMap) {

        StringBuilder lineSb = new StringBuilder();

        String[] sa;
        String[] saInner;
        Map<String, String> lineMap = new LinkedHashMap<>();

        sa = line.split("" + (char) (1));
        for (String sa1 : sa) {
            saInner = sa1.split("=");
            lineMap.put(saInner[0], saInner[1]);
        }

        //creating of header:
        String msgTyp;
        lineSb.append("MessageType: ");
        
        if (lineMap.containsKey("35")) {
            msgTyp = lineMap.get("35");
            if(messageMap.containsKey(msgTyp)){
                lineSb.append(messageMap.get(msgTyp)).append("\n");
            } else {
                lineSb.append("Unknown messageType!! \n");
            }
        } else {
            lineSb.append("MESSAGE w.o. messageType!! \n");
        }

        //creating final FIX message body:
        String k, v;
        for (Map.Entry<String, String> entry : lineMap.entrySet()) {
            k = entry.getKey();
            v = entry.getValue();
            lineSb.append(k).append(" ");
            if (fieldMap.containsKey(k)) {
                lineSb.append(fieldMap.get(k)).append(" ");
            } else {
                lineSb.append("Unknown description ");
            }
            lineSb.append(v).append("; ");
            //s = tmp.toString();
            //System.out.println("*" + entry.getKey() + "/" + entry.getValue() + "*");
        }
        lineSb.append("\n\n");

        return lineSb.toString();
    }
}
