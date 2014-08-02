/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Veres
 */
public class MyXMLHandler extends DefaultHandler {

    public Map<String, String> fieldMap = null;
    public Map<String, String> messageMap = null;
    
    public MyXMLHandler(){
        this.fieldMap = new HashMap<>();
        this.messageMap = new HashMap<>();
    }
    

    @Override
    public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException {

        //for message:
        String msgType, msgName;
        //for field:
        String fieldNum, fieldName;
        
        switch (qName) {
            case "message":
                msgType = attributes.getValue("msgtype");
                msgName = attributes.getValue("name");
                messageMap.put(msgType, msgName);
                break;
            case "field":
                if (attributes.getValue("number") != null) {
                    fieldNum = attributes.getValue("number");
                    fieldName = attributes.getValue("name");
                    fieldMap.put(fieldNum, fieldName);
                }
                break;
            default:
                break;
            }
        }
    
        @Override
        public void endElement
        (String uri, String localName ,	String qName) throws SAXException {
            //no need to implement
        }

        @Override
        public void characters
        (char ch[], int start, int length) throws SAXException {
            //not need to implement, there is no use of texts in FIX ...XML file.
        }
        
        
        //getters:
        public Map<String, String> getFieldMap(){
            return this.fieldMap;
        }
        
        public Map<String, String> getMessageMap(){
            return this.messageMap;
        }
    }