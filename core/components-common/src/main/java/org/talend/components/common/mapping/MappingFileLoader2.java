// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.common.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * DB configuration parser (mapping_*.xml)
 */
public class MappingFileLoader2 {

    public List<Dbms> load(File file) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder analyser = documentBuilderFactory.newDocumentBuilder();
            Document document = analyser.parse(file);
            NodeList dbmsNodes = document.getElementsByTagName("dbms");
            return constructAllDbms(dbmsNodes);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Constructs all DBMS from DOM
     * 
     * @param dbmsNodes
     * @return
     */
    private List<Dbms> constructAllDbms(NodeList dbmsNodes) {
        ArrayList<Dbms> dbmsList = new ArrayList<>();
        for (int i = 0; i < dbmsNodes.getLength(); i++) {
            Dbms dbms = constructDbms((Element) dbmsNodes.item(i));
            dbmsList.add(dbms);
        }
        return dbmsList;
    }
    
    /**
     * Construct DBMS from DOM Element
     * 
     * @param dbmsNode
     * @return
     */
    private Dbms constructDbms(Element dbmsNode) {
        String productName = dbmsNode.getAttribute("product");
        String id = dbmsNode.getAttribute("id");
        String label = dbmsNode.getAttribute("label");
        boolean isDefault = Boolean.parseBoolean(dbmsNode.getAttribute("default"));
        Dbms dbms = new Dbms(id, productName, label, isDefault);

        // parse db types
        Element dbTypesNode = getChildElement(dbmsNode, "dbTypes");
        NodeList dbTypes = dbTypesNode.getElementsByTagName("dbType");
        for (int i = 0; i < dbTypes.getLength(); i++) {
            dbms.addType(constructDbType((Element) dbTypes.item(i)));
        }
        
        // parse type mappings
        Element javaLanguageNode = findJavaLanguage(dbmsNode.getElementsByTagName("language"));
        // process talend to db mappings
        Element talendToDbTypes = getChildElement(javaLanguageNode, "talendToDbTypes");
        List<Element> talendMappings = getChildren(talendToDbTypes);
        int i = 0;
        for (Element el : talendMappings) {
            System.out.println(el.getTagName());
            constructTalendMapping(el);
        }

        return dbms;
    }
    
    private Element findJavaLanguage(NodeList languagesNodeList) {
        for (int i = 0; i < languagesNodeList.getLength(); i++) {
            Element languageNode = (Element) languagesNodeList.item(0);
            if (languageNode.getAttribute("name").equals("java")) {
                return languageNode;
            }
        }
        return null; // mapping file has no mapping for java
    }
    
    /**
     * Constructs db type from DOM object
     * 
     * @param dbTypeNode
     */
    private DbType constructDbType(Element dbTypeNode) {
        String typeName = dbTypeNode.getAttribute("type");
        
        boolean isDefault = false;
        String isDefaultAttribute = dbTypeNode.getAttribute("default");
        if (!isDefaultAttribute.isEmpty()) {
            isDefault = Boolean.parseBoolean(isDefaultAttribute);
        }
        
        int defaultLength = DbType.UNDEFINED;
        String defaultLengthAttribute = dbTypeNode.getAttribute("defaultLength");
        if (!defaultLengthAttribute.isEmpty()) {
            defaultLength = Integer.parseInt(defaultLengthAttribute);
        }
        
        int defaultPrecision = DbType.UNDEFINED;
        String defaultPrecisionAttribute = dbTypeNode.getAttribute("defaultPrecision");
        if (!defaultPrecisionAttribute.isEmpty()) {
            defaultPrecision = Integer.parseInt(defaultPrecisionAttribute);
        }
        
        boolean ignoreLen = false;
        String ignoreLenAttribute = dbTypeNode.getAttribute("ignoreLen");
        if (!ignoreLenAttribute.isEmpty()) {
            ignoreLen = Boolean.parseBoolean(ignoreLenAttribute);
        }
        
        boolean ignorePre = false;
        String ignorePreAttribute = dbTypeNode.getAttribute("ignorePre");
        if (!ignorePreAttribute.isEmpty()) {
            ignorePre = Boolean.parseBoolean(ignorePreAttribute);
        }
        
        boolean preBeforeLen = false;
        String preBeforeLenAttribute = dbTypeNode.getAttribute("preBeforeLen");
        if (!preBeforeLenAttribute.isEmpty()) {
            preBeforeLen = Boolean.parseBoolean(preBeforeLenAttribute);
        }
        
        return new DbType(typeName, isDefault, defaultLength, defaultPrecision, ignoreLen, ignorePre, preBeforeLen);
    }
    
    private void constructTalendMapping(Element talendMapping) {
        String talendType = talendMapping.getAttribute("type");
        System.out.println(talendType);
        // find corresponding Talend type object
        
        List<Element> correspondingDbTypes = getChildren(talendMapping);
        for (Element dbTypeNode : correspondingDbTypes) {
            String dbType = dbTypeNode.getAttribute("type");
            System.out.println(dbType);
            String defaultAttrValue = dbTypeNode.getAttribute("default");
            boolean isDefault = defaultAttrValue.isEmpty() ? false : Boolean.parseBoolean(defaultAttrValue);
            System.out.println(isDefault);
        }
        
        
    }
    
    private Element getChildElement(Element parent, String tagName) {
        return (Element) parent.getElementsByTagName(tagName).item(0);
    }
    
    /**
     * Get children of type ELEMENT_NODE from parent <code>parentNode</code>.
     * 
     * @param parentNode
     * @return
     */
    private List<Element> getChildren(Node parentNode) {
        Node childNode = parentNode.getFirstChild();
        ArrayList<Element> list = new ArrayList<>();
        while (childNode != null) {
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                list.add((Element)childNode);
            }
            childNode = childNode.getNextSibling();
        }
        return list;
    }
}
