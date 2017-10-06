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

import java.util.HashMap;
import java.util.Map;

public enum TalendType implements DataType {
    LIST("id_List"),
    BOOLEAN("id_Boolean"),
    BYTE("id_Byte"),
    BYTES("id_byte[]"),
    CHARACTER("id_Character"),
    DATE("id_Date"),
    BIG_DECIMAL("id_BigDecimal"),
    DOUBLE("id_Double"),
    FLOAT("id_Float"),
    INTEGER("id_Integer"),
    LONG("id_Long"),
    OBJECT("id_Object"),
    SHORT("id_Short"),
    STRING("id_String");

    private static final Map<String, TalendType> talendTypes = new HashMap<>();

    static {
        for (TalendType talendType : values()) {
            talendTypes.put(talendType.typeName, talendType);
        }
    }

    private final String typeName;

    private TalendType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getName() {
        return typeName;
    }

    public static TalendType get(String typeName) {
        TalendType talendType = talendTypes.get(typeName);
        if (talendType == null) {
            throw new IllegalArgumentException(String.format("Invalid value %s, it should be one of %s", typeName, talendTypes));
        }
        return talendType;
    }

}
