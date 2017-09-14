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
        for (int dbmsIndex = 0; dbmsIndex < dbmsNodes.getLength(); dbmsIndex++) {
            Dbms dbms = constructDbms((Element) dbmsNodes.item(dbmsIndex));
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
        NamedNodeMap dbmsAttributes = dbmsNode.getAttributes();
        String productName = dbmsAttributes.getNamedItem("product").getNodeValue(); //$NON-NLS-1$
        String id = dbmsAttributes.getNamedItem("id").getNodeValue(); //$NON-NLS-1$
        String label = dbmsAttributes.getNamedItem("label").getNodeValue(); //$NON-NLS-1$
        boolean isDefault = Boolean.parseBoolean(dbmsAttributes.getNamedItem("default").getNodeValue()); //$NON-NLS-1$
        
        Dbms dbms = new Dbms(id, productName, label, isDefault);
        
        List<Node> childrenOfDbmsNode = getChildElementNodes(dbmsNode);
        
        return dbms;
    }
    
    /**
     * Get children of type ELEMENT_NODE from parent <code>parentNode</code>.
     * 
     * @param parentNode
     * @return
     */
    private List<Node> getChildElementNodes(Node parentNode) {
        Node childNode = parentNode.getFirstChild();
        ArrayList<Node> list = new ArrayList<Node>();
        while (childNode != null) {
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                list.add(childNode);
            }
            childNode = childNode.getNextSibling();
        }
        return list;
    }
}
