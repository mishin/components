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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents type mapping for specific data source (former Dbms)
 */
public class DataSourceTypes<T extends DataType> {

    private final String id;

    private final String product;

    private final String label;

    private T defaultType;

    private boolean isDefault;

    private List<T> types;
    
    private Map<T, TypeMapping<T, TalendType>> dataSourceTypesMapping;
    
    private Map<TalendType, TypeMapping<TalendType, T>> talendTypesMapping;
    
    public DataSourceTypes(String id, String product, String label) {
        this.id = id;
        this.product = product;
        this.label = label;
    }
    
    public List<String> getTypeNames() {
        ArrayList typeNames = new ArrayList<>(types.size());
        for (T type : types) {
            typeNames.add(type.getName());
        }
        return typeNames;
    }
    
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
