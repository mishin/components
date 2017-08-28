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

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class TypeMapping<SourceT extends DataType, TargetT extends DataType> {

    private final SourceT sourceType;
    
    private final TargetT defaultTargetType;
    
    private final Set<TargetT> alternativeTargetTypes;
    
    public TypeMapping(SourceT sourceType, TargetT defaultTargetType, Set<TargetT> alternativeTargetTypes) {
        this.sourceType = sourceType;
        this.defaultTargetType = defaultTargetType;
        this.alternativeTargetTypes = new HashSet<>(alternativeTargetTypes);
    }
}
