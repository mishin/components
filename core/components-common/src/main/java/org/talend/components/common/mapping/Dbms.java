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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * DOC amaumont TypesManager class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class Dbms implements IDbms {

    private final String id;

    private final String product;

    private final String label;

    private final boolean defaultDbms;

    private final Map<String, DbType> types = new HashMap<>();

    private final Map<String, TypeMapping<TalendType, DbType>> talend2DbTypes = new HashMap<>();

    private final Map<String, TypeMapping<DbType, TalendType>> dbType2Talend = new HashMap<>();

    private String defaultDbType;

    /**
     * Constructor sets id, product, label and whether DBMS is default
     * 
     * @param id
     * @param product
     * @param label
     * @param isDefault
     */
    public Dbms(String id, String product, String label, boolean isDefault) {
        this.id = id;
        this.product = product;
        this.label = label;
        this.defaultDbms = isDefault;
    }

    /**
     * Getter for dbmsTypes.
     * 
     * @return the dbmsTypes
     */
    public List<String> getDbTypes() {
        return new ArrayList<String>(types.keySet());
    }

    /**
     * Getter for id.
     * 
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter for label.
     * 
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Getter for product.
     * 
     * @return the product
     */
    public String getProduct() {
        return this.product;
    }

    /**
     * Getter for defaultDbType.
     * 
     * @return the defaultDbType
     */
    public String getDefaultDbType() {
        return this.defaultDbType;
    }

    public DbType getDbType(String dbTypeName) {
        return types.get(dbTypeName);
    }

    /**
     * toString method: creates a String representation of the object
     * 
     * @return the String representation
     * @author
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Dbms["); //$NON-NLS-1$
        buffer.append("product = ").append(product); //$NON-NLS-1$
        buffer.append(", id = ").append(id); //$NON-NLS-1$
        buffer.append(", label = ").append(label); //$NON-NLS-1$
        buffer.append(", defaultDbType = ").append(defaultDbType); //$NON-NLS-1$
        buffer.append(", dbmsTypes = ").append(getDbTypes()); //$NON-NLS-1$
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }

    /**
     * Add database type. It is used by configuration parser to fill this Dbms object
     * 
     * @param typeName name of required database type
     * @param type
     */
    void addType(String typeName, DbType type) {
        types.put(typeName, type);
    }

    /**
     * Returns required database type
     * 
     * @param typeName name of required database type
     * @return database type
     */
    public DbType getType(String typeName) {
        return types.get(typeName);
    }
    
    public TypeMapping<TalendType, DbType> getTalendMapping(String talendTypeName) {
        return talend2DbTypes.get(talendTypeName);
    }
    
    public TypeMapping<DbType, TalendType> getDbMapping(String dbTypeMapping) {
        return dbType2Talend.get(dbTypeMapping);
    }

    public List<TalendType> getAdvisedTalendTypes(String dbTypeName) {
        return null;
    }

    public List<DbType> getAdvisedDbTypes(String talendTypeName) {
        return null;
    }

    public boolean isAdvisedTalendType(String talendType, String dbType) {
        return false;
    }

    public boolean isAdvisedDbType(String dbType, String talendType) {
        return false;
    }

    void addTalendMapping(String sourceTypeName, TypeMapping<TalendType, DbType> mapping) {
        talend2DbTypes.put(sourceTypeName, mapping);
    }

    void addDbMapping(String sourceTypeName, TypeMapping<DbType, TalendType> mapping) {
        dbType2Talend.put(sourceTypeName, mapping);
    }

    /**
     * Getter for defaultDbms.
     * 
     * @return the defaultDbms
     */
    boolean isDefaultDbms() {
        return defaultDbms;
    }

    /**
     * Sets the defaultDbms.
     * 
     * @param defaultDbms
     * the defaultDbms to set
     */
    void setDefaultDbms(boolean defaultDbms) {
        this.defaultDbms = defaultDbms;
    }

}
