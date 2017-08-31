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
import java.util.List;

import org.junit.Test;

/**
 *
 */
public class MappingFileLoader2Test {

    @Test
    public void testLoad() {
        File mappingFile = new File("src/test/resources/org/talend/components/common/mapping/mapping_mysql.xml");
        MappingFileLoader2 fileLoader = new MappingFileLoader2();
        List<Dbms> dbmsList = fileLoader.load(mappingFile);
    }
}
