// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Load Metadata Talend Type from mappingMetadataTypes.xml. Talend Types available in the application
 * 
 * 
 * $Id: MetadataTalendType.java 38013 2010-03-05 14:21:59Z mhirt $
 * 
 */
public final class MetadataTalendType {

    public static final String INTERNAL_MAPPINGS_FOLDER = "mappings"; //$NON-NLS-1$

    public static final String PROJECT_MAPPING_FOLDER = ".settings/mappings"; //$NON-NLS-1$

    private static Set<Dbms> dbmsSet = new HashSet<Dbms>();

    private static List<File> metadataMappingFiles = null;

    /**
     * Default Constructor. Must not be used.
     */
    private MetadataTalendType() {
        
    }

    /**
     * Returns all Dbms. Loads them if they are not loaded yet
     * 
     * @return
     */
    public static Dbms[] getAllDbmsArray() {
        if (dbmsSet.isEmpty()) {
            loadCommonMappings();
        }
        return dbmsSet.toArray(new Dbms[0]);
    }
    
    /**
     * Returns DBMS by ID
     * Retrieve and return the dbms from the given id.
     * 
     * @param dbmsId
     * @return the dbms from the given id
     */
    public static Dbms getDbmsById(String dbmsId) {
        if (dbmsId == null) {
            throw new IllegalArgumentException();
        }
        for (Dbms dbms : getAllDbmsArray()) {
            if (dbmsId.equals(dbms.getId())) {
                return dbms;
            }
        }
        return null;
    }

    /**
     * Returns DBMS by product
     * 
     * @param product
     * @return
     */
    public static Dbms getDbmsByProduct(String product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }
        for (Dbms dbms : getAllDbmsArray()) {
            if (product.equals(dbms.getProduct())) {
                return dbms;
            }
        }
        return null;
    }

    /**
     * Retrieve the DBMS from the given dbmsId and return sorted db types from it.
     * 
     * @param dbmsId
     * @return return DB types from the DBMS
     */
    public static String[] getDbTypes(String dbmsId) {
        if (dbmsId == null) {
            throw new IllegalArgumentException();
        }
        Dbms dbms = getDbmsById(dbmsId);
        if (dbms == null) {
            throw new IllegalArgumentException("Unknown dbmsId: '" + dbmsId + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String[] list = dbms.getDbTypes().toArray(new String[0]);
        Arrays.sort(list);
        return list;
    }
    
    /**
     * Loads all mapping files 
     * Load db types and mapping with the current activated language (Java, Perl, ...).
     * 
     */
    public static void loadCommonMappings() {
//        URL url = getProjectForderURLOfMappingsFile();
        URL url;
        try {
            url = new URL("test spec");
            File dir = new File(url.getPath());
            metadataMappingFiles = new ArrayList<File>();
            dbmsSet.clear();
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (file.getName().matches("^mapping_.*\\.xml$")) { //$NON-NLS-1$
                        loadMapping(file);
                        metadataMappingFiles.add(file);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load specific mapping (configuration) file
     * 
     * @param file
     */
    private static void loadMapping(File file) {
        MappingFileLoader mappingFileLoader = new MappingFileLoader();
        mappingFileLoader.load(file);
    }

    /**
     * Getter for dbmsSet.
     * 
     * @return the dbmsSet
     */
    static Set<Dbms> getDbmsSet() {
        return dbmsSet;
    }

    /**
     * Create and return a MappingTypeRetriever which helps to retrieve dbType from a talendType or the contrary.
     * 
     * @param dbmsId
     * @return new MappingTypeRetriever loaded with Dbms found with given dbmsId
     */
    public static MappingTypeRetriever getMappingTypeRetriever(String dbmsId) {
        if (dbmsId == null) {
            throw new IllegalArgumentException();
        }
        Dbms dbms = getDbmsById(dbmsId);
        if (dbms == null) {
            return null;
        }
        return new MappingTypeRetriever(dbms);
    }

    /**
     * DOC xqliu Comment method "getMappingTypeProduct".
     * 
     * @param product
     * @return
     */
    public static MappingTypeRetriever getMappingTypeRetrieverByProduct(String product) {
        if (product == null) {
            throw new IllegalArgumentException();
        }
        Dbms dbms = getDbmsByProduct(product);
        if (dbms == null) {
            return null;
        }
        return new MappingTypeRetriever(dbms);
    }

}
