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

//        private String id;
//
//        private String product;
//
//        private String label;
//
//        private String defaultDbType;
//
//        private boolean defaultDbms;
//
//        private List<String> dbmsTypes;
//
//        private Set<MappingType> dbToTalendTypes;
//
//        private Set<MappingType> talendToDbTypes;
//
//        private List<DbDefaultLengthAndPrecision> defaultLengthPrecision;
//
//        private List<DbIgnoreLengthAndPrecision> ignoreLengthPrecision;
//
//        private List<DbPreBeforeLength> prebeforelength;

        public List<DbPreBeforeLength> getPrebeforelength();

        public void setPrebeforelength(List<DbPreBeforeLength> prebeforelength);

        public List<DbDefaultLengthAndPrecision> getDefaultLengthPrecision() ;

        public void setDefaultLengthPrecision(List<DbDefaultLengthAndPrecision> defaultLengthPrecision);

//        /**
//         * DOC amaumont Dbms constructor comment.
//         * 
//         * @param id
//         * @param label
//         * @param dbmsTypes
//         * @param mappingTypes
//         */
//        public Dbms(String id, String label, boolean defaultDbms,
//                List<String> dbmsTypes) {
//            super();
//            this.id = id;
//            this.label = label;
//            this.dbmsTypes = dbmsTypes;
//            this.defaultDbms = defaultDbms;
//        }

//        /**
//         * DOC amaumont Dbms constructor comment.
//         * 
//         * @param dbmsIdValue
//         */
//        protected Dbms(String id) {
//            this.id = id;
//        }

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
//         * Sets the label.
//         * 
//         * @param label
//         *            the label to set
//         */
//        protected void setLabel(String label);

        /**
         * Getter for mappingTypes.
         * 
         * @return the mappingTypes
         */
        public Set<MappingType> getDbToTalendTypes();

//        /**
//         * Sets the mappingTypes.
//         * 
//         * @param mappingTypes
//         *            the mappingTypes to set
//         */
//        protected void setDbToTalendTypes(Set<MappingType> mappingTypes);

        /**
         * Getter for talendToDbTypes.
         * 
         * @return the talendToDbTypes
         */
        public Set<MappingType> getTalendToDbTypes();

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
//
//        /**
//         * Sets the product.
//         * 
//         * @param product
//         *            the product to set
//         */
//        protected void setProduct(String product);

        /**
         * Getter for defaultDbType.
         * 
         * @return the defaultDbType
         */
        public String getDefaultDbType();

//        /**
//         * Sets the defaultDbType.
//         * 
//         * @param defaultDbType
//         *            the defaultDbType to set
//         */
//        protected void setDefaultDbType(String defaultDbType);

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
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

        public List<DbIgnoreLengthAndPrecision> getIgnoreLengthPrecision();

        public void setIgnoreLengthPrecision(List<DbIgnoreLengthAndPrecision> ignoreLengthPrecision);



}
