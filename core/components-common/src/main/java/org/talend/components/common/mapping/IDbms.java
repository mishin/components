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

import java.util.List;
import java.util.Set;

/**
 * Technical interface to rewrite Dbms class
 */
public interface IDbms {

        /**
         * Getter for dbmsTypes.
         * 
         * @return the dbmsTypes
         */
        public List<String> getDbTypes();

//        /**
//         * Sets the dbmsTypes.
//         * 
//         * @param dbmsTypes
//         *            the dbmsTypes to set
//         */
//        protected void setDbmsTypes(List<String> dbmsTypes);

        /**
         * Getter for id.
         * 
         * @return the id
         */
        public String getId();

//        /**
//         * Sets the id.
//         * 
//         * @param id
//         *            the id to set
//         */
//        protected void setId(String id);

        /**
         * Getter for label.
         * 
         * @return the label
         */
        public String getLabel();

//        /**
//         * Sets the talendToDbTypes.
//         * 
//         * @param talendToDbTypes
//         *            the talendToDbTypes to set
//         */
//        protected void setTalendToDbTypes(Set<MappingType> talendToDbTypes);

        /**
         * Getter for product.
         * 
         * @return the product
         */
        public String getProduct();

        @Override
        public int hashCode();

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj);

        /**
         * toString method: creates a String representation of the object
         * 
         * @return the String representation
         * @author
         */
        public String toString();

}
