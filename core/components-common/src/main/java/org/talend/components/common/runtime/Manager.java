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
package org.talend.components.common.runtime;

public abstract class Manager {
    
    protected INode node;// add the Node, it will be more convenient
    public void setNode(INode node) {
       this.node = node;
    }
    
    protected String cid;        
    protected abstract String getDBMSId();
    protected abstract String getLProtectedChar();
    protected abstract String getRProtectedChar();        
    public Manager() {}
    public Manager(String cid) {
        this.cid = cid;
    }
  protected  String getLProtectedChar(String columName) {
      return getLProtectedChar();
  }
  protected  String getRProtectedChar(String columName) {
      return getRProtectedChar();        
  }
  protected boolean contaionsSpaces(String columnName) {
      if (columnName != null) {
          if (columnName.startsWith("\" + ") && columnName.endsWith(" + \"")) {
              return false;
          }
          
          if (columnName.contains(" ")) {
              return true;
          }
          // minus are also a problem
          if (columnName.contains("-")) {
              return true;
          }
      }
      return false;
  }

    public String getSelectionSQL() {
        StringBuilder selectionSQL = new StringBuilder();
        selectionSQL.append("SELECT COUNT(1) FROM " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar());
        return selectionSQL.toString();
    }
    public String getDeleteTableSQL() {
        StringBuilder deleteTableSQL = new StringBuilder();
        deleteTableSQL.append("DELETE FROM " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar());
        return deleteTableSQL.toString();
    }
    public String getTruncateTableSQL() {
        StringBuilder truncateTableSQL = new StringBuilder();
        truncateTableSQL.append("TRUNCATE TABLE " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar());
        return truncateTableSQL.toString();
    }
    public String getTruncateReuseStroageTableSQL() {
        StringBuilder truncate_reuse_stroage_TableSQL = new StringBuilder();
        truncate_reuse_stroage_TableSQL.append("TRUNCATE TABLE " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar() + " REUSE STORAGE " + getRProtectedChar());
        return truncate_reuse_stroage_TableSQL.toString();
    }
    public String getDropTableSQL() {
        StringBuilder dropTableSQL = new StringBuilder();
        dropTableSQL.append("DROP TABLE " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar());
        return dropTableSQL.toString();
    }
    
    public String getCreateTableSQL(List<Column> columnList) {
        MappingTypeRetriever mappingType = MetadataTalendType.getMappingTypeRetriever(getDBMSId());
        StringBuilder createSQL = new StringBuilder();
        createSQL.append("CREATE TABLE " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar() + "(");
        List<String> pkList = new ArrayList<String>();
        int count = 0;
        String ending = ",";
        
        for (Column column : columnList) {
            if (column.isReplaced()) {
                List<Column> replacedColumns = column.getReplacement();
                if (column.isKey()) {
                    for (Column replacedColumn : replacedColumns) {
                        pkList.add(getLProtectedChar(replacedColumn.getColumnName()) + replacedColumn.getColumnName() + getRProtectedChar(replacedColumn.getColumnName()));
                    }
                }
                int replacedCount = 0;
                for (Column replacedColumn : replacedColumns) {
                    if (count == columnList.size() - 1 && replacedCount == replacedColumns.size() - 1) {
                        ending = "";
                    }
                    createSQL.append(getLProtectedChar(replacedColumn.getColumnName()) + replacedColumn.getColumnName() + getRProtectedChar(replacedColumn.getColumnName()) + " ");
                    createSQL.append(replacedColumn.getDataType() + ending);
                    replacedCount++;
                }
            } else {
                if (count == columnList.size() - 1) {
                    ending = "";
                }
                
                if (column.isAddCol()) {
                    createSQL.append(getLProtectedChar( column.getColumnName() ) + column.getColumnName() + getRProtectedChar( column.getColumnName() ) + " ");
                    createSQL.append(column.getDataType() + ending);
                } else if(column.isDynamic()) {
                    createSQL.append(" {TALEND_DYNAMIC_COLUMN} ").append(ending);
                } else {
                    if (column.isKey()) {
                        pkList.add(getLProtectedChar( column.getColumnName() ) + column.getColumnName() + getRProtectedChar( column.getColumnName() ));
                    }
                    createSQL.append(getLProtectedChar( column.getColumnName() ) + column.getColumnName() + getRProtectedChar( column.getColumnName() ) + " ");
                    String dataType = null;
                    if (column.getColumn().getType() == null || column.getColumn().getType().trim().equals("")) {
                        dataType = mappingType.getDefaultSelectedDbType(column.getColumn().getTalendType());
                    } else {
                        dataType = column.getColumn().getType();
                    }
                    Integer length = column.getColumn().getLength() == null ? 0 : column.getColumn().getLength();
                    if (MYSQL.equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED")) {                            
                        createSQL.append(dataType.substring(0,dataType.indexOf("UNSIGNED"))) ;                            
                    }else if(ORACLE.equalsIgnoreCase(getDBMSId()) && dataType.matches("TIMESTAMP WITH TIME ZONE")){
                        createSQL.append("TIMESTAMP("+length+") WITH TIME ZONE");
                    }else{                            
                        createSQL.append(dataType);
                    }
                    Integer precision = column.getColumn().getPrecision() == null ? 0 : column.getColumn().getPrecision();
                    boolean lengthIgnored = mappingType.isLengthIgnored(getDBMSId(), dataType);
                    boolean precisionIgnored = mappingType.isPrecisionIgnored(getDBMSId(), dataType);
                    String prefix = "";
                    String suffix = "";
                    String comma = "";
                    
                    if ( (ORACLE.equalsIgnoreCase(getDBMSId()))
                            && (("NUMBER".equalsIgnoreCase(dataType)) || ("CHAR".equalsIgnoreCase(dataType)) || ("NCHAR".equalsIgnoreCase(dataType)))
                            && (column.getColumn().getLength() == null || 0 == column.getColumn().getLength())
                            && (column.getColumn().getPrecision() == null || 0 == column.getColumn().getPrecision())
                    ){} 
                    else if (((MYSQL.equalsIgnoreCase(getDBMSId()))||(AS400.equalsIgnoreCase(getDBMSId())))
                            && (("DECIMAL".equalsIgnoreCase(dataType)) || ("NUMERIC".equalsIgnoreCase(dataType))||(("FLOAT".equalsIgnoreCase(dataType)))||("DOUBLE".equalsIgnoreCase(dataType)))
                            && (column.getColumn().getLength() == null || 0 == column.getColumn().getLength())
                            && (column.getColumn().getPrecision() == null || 0 == column.getColumn().getPrecision())
                    ) {}
                    else {
                        if (mappingType.isPreBeforeLength(getDBMSId(), dataType)) {
                            if (!precisionIgnored) {
                                prefix = "(";
                                suffix = ") ";
                                createSQL.append(prefix + precision);
                            }
                            if (!lengthIgnored) {
                                prefix = (prefix.equals("") ? "(" : prefix);
                                suffix = (suffix.equals("") ? ") " : suffix);
                                if (precisionIgnored) {
                                    createSQL.append(prefix);
                                    comma = "";
                                } else {
                                    comma = ",";
                                }
                                createSQL.append(comma + length);
                            }
                            createSQL.append(suffix);
                        } else {
                            if (!lengthIgnored) {
                                if ((POSTGRESQL.equalsIgnoreCase(getDBMSId()) || POSTGREPLUS.equalsIgnoreCase(getDBMSId()) ) && column.getColumn().getLength() == null) {                                    
                                } else { 
                                    prefix = "(";
                                    suffix = ") ";
                                    createSQL.append(prefix + length);                                    
                                }
                            }
                            if (!precisionIgnored) {
                                prefix = (prefix.equals("") ? "(" : prefix);
                                suffix = (suffix.equals("") ? ") " : suffix);
                                if (lengthIgnored) {
                                    createSQL.append(prefix);
                                    comma = "";
                                } else {
                                    comma = ",";
                                }
                                createSQL.append(comma + precision);
                            }
                            if ((POSTGRESQL.equalsIgnoreCase(getDBMSId()) || POSTGREPLUS.equalsIgnoreCase(getDBMSId()) ) && column.getColumn().getLength() == null) {                                
                            } else {
                                createSQL.append(suffix);
                            }
                            if(MYSQL.equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED")) {
                                createSQL.append("UNSIGNED");
                            }
                        }                            
                        
                    }
                    if(column.isAutoIncrement()) {
                        // move the autoincrease key column to the first index in the primary defination. Otherwise, it will throw exception
                        if(MYSQL.equalsIgnoreCase(getDBMSId()) ) {
                            String columnStr = getLProtectedChar( column.getColumnName() ) + column.getColumnName() + getRProtectedChar( column.getColumnName() );
                            int index = pkList.indexOf(columnStr);
                            if(index !=-1){
                                for (int i=index;i>0;i--) {
                                    pkList.set(i, pkList.get(i-1));
                                }
                                pkList.set(0, columnStr);
                            }
                        }
                        createSQL.append(getAutoIncrement(column.getStartValue(), column.getStep()));
                    } else {
                        createSQL.append(setDefaultValue(column.getColumn().getDefault(), dataType));
                        if(DB2.equalsIgnoreCase(getDBMSId())&&column.isKey()){
                            createSQL.append(" not null ");
                        }else{
                            createSQL.append(setNullable(column.getColumn().isNullable()));
                        }
                    }

                    createSQL.append(ending);
                }
            }
            count++;
        }
        
        if (pkList.size() > 0) {
            createSQL.append(",primary key(");
            int i = 0;
            for (String pk : pkList) {
                createSQL.append(pk);
                if (i != pkList.size() - 1) {
                    createSQL.append(",");
                }
                i++;
            }
            createSQL.append(")");
        }
        return createSQL.toString();
    }
    protected String getAutoIncrement(int startValue, int step) {
        if(SYBASE.equalsIgnoreCase(getDBMSId()) || EXASOL.equalsIgnoreCase(getDBMSId())){
            return " IDENTITY NOT NULL";
        } else if(MYSQL.equalsIgnoreCase(getDBMSId()) ) {
            return " AUTO_INCREMENT";
        } else if(DB2.equalsIgnoreCase(getDBMSId())){
            return " Generated by default AS IDENTITY(START WITH "+startValue+", INCREMENT BY "+step+", NO CACHE )";
        } else if(TERADATA.equalsIgnoreCase(getDBMSId())){
            return " generated by default as identity (start with " + startValue + " increment by " + step + " no cycle) not null";
        }else {
            return " IDENTITY (" + startValue + ", " + step + ") NOT NULL";
        }
    }
    protected String setNullable(boolean nullable) {
        if(!nullable) {
            return " not null ";
        } else {
            return "";
        }
    }
    protected String setDefaultValue(String defaultValue, String columnType) {
        if (defaultValue == null || defaultValue.equals("\"\"") || defaultValue.equals("")) {
            return " ";
        } else if ((defaultValue.startsWith("\"") || defaultValue.startsWith("'"))
                && (defaultValue.endsWith("\"") || defaultValue.endsWith("'"))) {
            return " default '" + defaultValue.substring(1, defaultValue.length() - 1) + "' ";
        } else if (defaultValue.equalsIgnoreCase("null")) {
            return " default null ";
        } else {
            return " default " + defaultValue + " ";
        }
    }
    public String getUpdateBulkSQL(List<IMetadataColumn> columnList) {
        StringBuilder updateBulkSQL = new StringBuilder();
        StringBuilder updateSetStmt = new StringBuilder();
        StringBuilder updateWhereStmt = new StringBuilder();
        updateBulkSQL.append("UPDATE " + getLProtectedChar() + "\" +  tableName_" + cid + " + \"" + getRProtectedChar() + ", " + getLProtectedChar() + "\" + tmpTableName_" + cid + " + \"" + getRProtectedChar());
        boolean firstKeyColumn = true;
        boolean firstUpdateColumn = true;
        String keySeparator = null;
        String updateSeparator = null;
        for(IMetadataColumn column : columnList) {
            if(column.isKey()) {
                if(firstKeyColumn) {
                    keySeparator = "";
                    firstKeyColumn = false;
                    updateWhereStmt.append(" WHERE \" + \"");
                } else {
                    keySeparator = " AND ";
                }
                updateWhereStmt.append(keySeparator);                    
                updateWhereStmt.append(getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar() + "." + getLProtectedChar() + "\" + \"" + column.getOriginalDbColumnName()  + "\" + \"" + getRProtectedChar() + " = " + getLProtectedChar() + "\" + tmpTableName_" + cid + " + \"" + getRProtectedChar() + "." + getLProtectedChar() + "\" + \"" + column.getOriginalDbColumnName()  + "\" + \"" + getRProtectedChar());
            } else {
                if(firstUpdateColumn) {
                    updateSeparator = "";
                    firstUpdateColumn = false;
                    updateSetStmt.append(" SET \" + \"");
                } else {
                    updateSeparator = ", ";
                }
                updateSetStmt.append(updateSeparator);
                updateSetStmt.append(getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar() + "." + getLProtectedChar() + "\" + \"" + column.getOriginalDbColumnName()  + "\" + \"" + getRProtectedChar() + " = " + getLProtectedChar() + "\" + tmpTableName_" + cid + " + \"" + getRProtectedChar() + "." + getLProtectedChar() + "\" + \"" + column.getOriginalDbColumnName()  + "\" + \"" + getRProtectedChar());
            }
        }
        return updateBulkSQL.toString() + updateSetStmt.toString() + updateWhereStmt.toString();
    }

    public List<Column> createColumnList(List<IMetadataColumn> columnList, boolean useFieldOptions, List<Map<String, String>> fieldOptions, List<Map<String, String>> addCols, boolean isSpecifyIdentityKey, String identityKey, int startValue, int step) {
        List<Column> stmtStructure = createColumnList(columnList, useFieldOptions, fieldOptions, addCols);
        if(isSpecifyIdentityKey) {
            for(Column column : stmtStructure) {
                if(column.name.equals(identityKey)) {
                    column.setAutoIncrement(true);
                    column.setStartValue(startValue);
                    column.setStep(step);
                    break;
                }
            }
        }
        return stmtStructure;
    }
    
    private boolean dynamicColumnIsReplaced = false;
    
    public List<Column> createColumnList(List<IMetadataColumn> columnList, boolean useFieldOptions, List<Map<String, String>> fieldOptions, List<Map<String, String>> addCols) {
        List<Column> stmtStructure = new ArrayList<Column>();            
        for(IMetadataColumn column : columnList) {
            Map<String, String> fieldOption = null;
            if(fieldOptions != null && fieldOptions.size() > 0) {
                for(Map<String, String> tmpFieldOption : fieldOptions) {
                    if(column.getLabel().equals(tmpFieldOption.get("SCHEMA_COLUMN"))) {
                        fieldOption = tmpFieldOption;
                        break;
                    }
                }
            }
            
            Column skeletonColumn = getColumn(column, column.isKey(), useFieldOptions, fieldOption);
            skeletonColumn.setOperator("=");
            skeletonColumn.setDataType(column.getType());
            stmtStructure.add(skeletonColumn);
            
            if("id_Dynamic".equals(column.getTalendType())) {
                skeletonColumn.setDynamic(true);
            }
        }
        
        dynamicColumnIsReplaced = false;
        
        if(addCols != null && addCols.size() > 0) {
            for(IMetadataColumn column : columnList) {
                for(Map<String, String> additionColumn : addCols) {
                    if(additionColumn.get("REFCOL").equals(column.getLabel())) {
                        int stmtIndex = 0;
                        for(Column stmtStr : stmtStructure){          
                            if(stmtStr.getName().equals(additionColumn.get("REFCOL"))) {
                                break;
                            }
                            stmtIndex++;
                        }           
                        if(additionColumn.get("POS").equals("AFTER")) {
                            Column insertAfter = getColumn("\" + " + additionColumn.get("NAME") + " + \"", "\" + " + additionColumn.get("SQL") + " + \"", true);
                            insertAfter.setDataType(additionColumn.get("DATATYPE"));
                            insertAfter.setOperator("=");
                            stmtStructure.add(stmtIndex+1, insertAfter);
                        } else if(additionColumn.get("POS").equals("BEFORE")) {
                            Column insertBefore = getColumn("\" + " + additionColumn.get("NAME") + " + \"", "\" + " + additionColumn.get("SQL") + " + \"", true);
                            insertBefore.setDataType(additionColumn.get("DATATYPE"));
                            insertBefore.setOperator("=");
                            stmtStructure.add(stmtIndex, insertBefore);
                        } else if(additionColumn.get("POS").equals("REPLACE")) {
                            Column replacementCol = getColumn("\" + " + additionColumn.get("NAME") + " + \"", "\" + " + additionColumn.get("SQL") + " + \"", true);
                            replacementCol.setDataType(additionColumn.get("DATATYPE"));
                            replacementCol.setOperator("=");                                
                            Column replacedCol = (Column) stmtStructure.get(stmtIndex);
                            replacementCol.setKey(replacedCol.isKey());
                            replacementCol.setUpdateKey(replacedCol.isUpdateKey());
                            replacementCol.setDeleteKey(replacedCol.isDeleteKey());
                            replacementCol.setUpdatable(replacedCol.isUpdatable());
                            replacementCol.setInsertable(replacedCol.isInsertable());
                            replacedCol.replace(replacementCol);
                            
                            if(replacedCol.isDynamic()) {
                                dynamicColumnIsReplaced = true;
                            }
                        }                            
                    }
                }
            }
        }
        
        return stmtStructure;
    }
    
    public boolean isDynamicColumnReplaced() {
        return dynamicColumnIsReplaced;
    }
    
    public Map<String, StringBuilder> createProcessSQL(List<Column> stmtStructure) {
        Map<String, StringBuilder> actionSQLMap = new HashMap<String, StringBuilder>();
        if(stmtStructure==null || stmtStructure.size() < 1) {
            actionSQLMap.put(INSERT_COLUMN_NAME, new StringBuilder());
            actionSQLMap.put(INSERT_VALUE_STMT, new StringBuilder());
            actionSQLMap.put(UPDATE_SET_STMT, new StringBuilder());
            actionSQLMap.put(UPDATE_WHERE_STMT, new StringBuilder());
            actionSQLMap.put(DELETE_WHERE_STMT, new StringBuilder());
            actionSQLMap.put(FIRST_UPDATE_KEY, new StringBuilder());
            actionSQLMap.put(FIRST_DELETE_KEY, new StringBuilder());
            actionSQLMap.put(FIRST_INSERT_COLUMN, new StringBuilder());
            actionSQLMap.put(FIRST_UPDATE_COLUMN, new StringBuilder());
        } else {
            for(Column column : stmtStructure) {
                if(column.isReplaced()) {
                    List<Column> replacedColumns = column.getReplacement();
                    for(Column replacedColumn : replacedColumns) {
                        actionSQLMap = processSQLClause(replacedColumn, actionSQLMap);
                    }
                } else {
                    actionSQLMap = processSQLClause(column, actionSQLMap);        
                }
            }
        }
        return actionSQLMap;
    }
    
    private boolean isSpecifyIdentityKey = false;
    
    public void setIsSpecifyIdentityKey(boolean isSpecifyIdentityKey) {
        this.isSpecifyIdentityKey = isSpecifyIdentityKey;
    }
    
    private Map<String, StringBuilder> processSQLClause(Column column, Map<String, StringBuilder> actionSQLMap) {
        StringBuilder insertColName = actionSQLMap.get(INSERT_COLUMN_NAME);
        if(insertColName == null) {
            insertColName = new StringBuilder();
        }
        StringBuilder insertValueStmt = actionSQLMap.get(INSERT_VALUE_STMT);
        if(insertValueStmt == null) {
            insertValueStmt = new StringBuilder();
        }
        StringBuilder updateSetStmt = actionSQLMap.get(UPDATE_SET_STMT);
        if(updateSetStmt == null) {
            updateSetStmt = new StringBuilder(); 
        }
        StringBuilder updateWhereStmt = actionSQLMap.get(UPDATE_WHERE_STMT);
        if(updateWhereStmt == null) {
            updateWhereStmt = new StringBuilder();
        }
        StringBuilder deleteWhereStmt = actionSQLMap.get(DELETE_WHERE_STMT);
        if(deleteWhereStmt == null) {
            deleteWhereStmt = new StringBuilder();
        }
        StringBuilder firstUpdateKeyColumn = actionSQLMap.get(FIRST_UPDATE_KEY);
        if(firstUpdateKeyColumn == null) {
            firstUpdateKeyColumn = new StringBuilder("true");
        }
        StringBuilder firstDeleteKeyColumn = actionSQLMap.get(FIRST_DELETE_KEY);
        if(firstDeleteKeyColumn == null) {
            firstDeleteKeyColumn = new StringBuilder("true");
        }
        StringBuilder firstInsertColumn = actionSQLMap.get(FIRST_INSERT_COLUMN);
        if(firstInsertColumn == null) {
            firstInsertColumn = new StringBuilder("true");
        }
        StringBuilder firstUpdateColumn = actionSQLMap.get(FIRST_UPDATE_COLUMN);
        if(firstUpdateColumn == null) {
            firstUpdateColumn = new StringBuilder("true");
        }
        String suffix = null;
        String separate = null;
        String identityKey = ElementParameterParser.getValue(node, "__IDENTITY_FIELD__");
        if(!(isSpecifyIdentityKey && (column.getName().equals(identityKey))) && column.isInsertable() && !column.isDynamic()) {
            if(firstInsertColumn.toString().equals("true")) {
                suffix = "";
                firstInsertColumn = new StringBuilder("false");
            } else {
                suffix = ",";
            }
            insertColName.append(suffix);
            insertColName.append(getLProtectedChar(column.getColumnName()) + column.getColumnName() + getRProtectedChar(column.getColumnName()));
            insertValueStmt.append(suffix);
            insertValueStmt.append(column.getSqlStmt());
        }
        if(column.isUpdatable() && !column.isDynamic()) {
            if(firstUpdateColumn.toString().equals("true")) {
                suffix = "";
                firstUpdateColumn = new StringBuilder("false");
            } else {
                suffix = ",";
            }
            updateSetStmt.append(suffix);
            updateSetStmt.append(getLProtectedChar(column.getColumnName()) + column.getColumnName() + getRProtectedChar(column.getColumnName()) + " " + column.getOperator() + " " + column.getSqlStmt());
        }
        if(column.isDeleteKey() && !column.isDynamic()) {
            if(firstDeleteKeyColumn.toString().equals("true")) {
                separate = "";
                firstDeleteKeyColumn = new StringBuilder("false");
            } else {
                separate = " AND ";
            }
            deleteWhereStmt.append(separate);
            
            //feature:2880
            whereStmtSupportNull(deleteWhereStmt, column);                 
        }
        if(column.isUpdateKey() && !column.isDynamic()) {
            if(firstUpdateKeyColumn.toString().equals("true")) {
                separate = "";
                firstUpdateKeyColumn = new StringBuilder("false");
            } else {
                separate = " AND ";
            }
            updateWhereStmt.append(separate);
            
            //feature:2880
            whereStmtSupportNull(updateWhereStmt, column);                
                                        
        }
        actionSQLMap.put(INSERT_COLUMN_NAME, insertColName);
        actionSQLMap.put(INSERT_VALUE_STMT, insertValueStmt);
        actionSQLMap.put(UPDATE_SET_STMT, updateSetStmt);
        actionSQLMap.put(UPDATE_WHERE_STMT, updateWhereStmt);
        actionSQLMap.put(DELETE_WHERE_STMT, deleteWhereStmt);
        actionSQLMap.put(FIRST_UPDATE_KEY, firstUpdateKeyColumn);
        actionSQLMap.put(FIRST_DELETE_KEY, firstDeleteKeyColumn);
        actionSQLMap.put(FIRST_INSERT_COLUMN, firstInsertColumn);
        actionSQLMap.put(FIRST_UPDATE_COLUMN, firstUpdateColumn);
        return actionSQLMap;
    }
    public String getGenerateType(String typeToGenerate) {
        if(typeToGenerate.equals("byte[]")) {
            typeToGenerate = "Bytes";
        } else if(typeToGenerate.equals("java.util.Date")) {
            typeToGenerate = "Date";
        } else if(typeToGenerate.equals("Integer")) {
            typeToGenerate = "Int";
        } else if(typeToGenerate.equals("List")) {  
            typeToGenerate = "Object";                 
        } else {
            typeToGenerate=typeToGenerate.substring(0,1).toUpperCase()+typeToGenerate.substring(1);
        }
        return typeToGenerate;
    }
    
    public String generateSetStmt(String typeToGenerate, String dbType, Column column, int index, String incomingConnName, String cid, int actionType) {
        return generateSetStmt(typeToGenerate, dbType, column, index, incomingConnName, cid, actionType, null); 
    }
    
    public String generateSetStmt(String typeToGenerate, String dbType, Column column, int index, String incomingConnName, String cid, int actionType, String dynamic) {
        
        if(dynamic==null) {
            dynamic="";
        } else {
            dynamic+=cid;
        }
        
        boolean isObject = false;
        String prefix = null;
        if(actionType == NORMAL_TYPE) {
            prefix = "pstmt_";
        } else if(actionType == INSERT_TYPE) {
            prefix = "pstmtInsert_";
        } else if(actionType == UPDATE_TYPE) {
            prefix = "pstmtUpdate_";
        }
        StringBuilder setStmt = new StringBuilder();
        if(typeToGenerate.equals("Character")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CHAR);\r\n");                
        } else if(typeToGenerate.equals("Date")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.DATE);\r\n");                
        } else if(typeToGenerate.equals("byte[]")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            if(dbType != null && (dbType.equals("LONG RAW") || dbType.equals("RAW"))) {
                setStmt.append(prefix + cid + ".setBytes(" + index + dynamic + ", null);\r\n");
            } else {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.ARRAY);\r\n");                    
            }                
        } else if(typeToGenerate.equals("Long") || typeToGenerate.equals("Byte") || typeToGenerate.equals("Integer") || typeToGenerate.equals("Short")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.INTEGER);\r\n");                
        } else if(typeToGenerate.equals("String")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            if(dbType != null && dbType.equals("CLOB")) {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CLOB);\r\n");                    
            } else {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.VARCHAR);\r\n");                    
            }                
        } else if(typeToGenerate.equals("Object")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            if(dbType != null && dbType.equals("BLOB")) {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.BLOB);\r\n");
            } else if("CLOB".equals(dbType)){
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CLOB);\r\n");
            } else {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.OTHER);\r\n");                    
            }               
        } else if(typeToGenerate.equals("Boolean")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.BOOLEAN);\r\n");                
        } else if(typeToGenerate.equals("Double")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.DOUBLE);\r\n");                
        } else if(typeToGenerate.equals("Float")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.FLOAT);\r\n");                
        }
        if(isObject) {
            setStmt.append("} else {");
        }
        typeToGenerate = getGenerateType(typeToGenerate);
        if(typeToGenerate.equals("Char") || typeToGenerate.equals("Character")) {
            if(isObject) {
                setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n"); 
            } else {
                setStmt.append("if(String.valueOf(" + incomingConnName + "." + column.getName() + ").toLowerCase().equals(\"null\")) {\r\n");
            }
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CHAR);\r\n");
            setStmt.append("} else if(" + incomingConnName + "." + column.getName() + " == '\0'){\r\n");
            setStmt.append(prefix + cid + ".setString(" + index + dynamic + ", \"\");\r\n");
            setStmt.append("} else {\r\n");
            setStmt.append(prefix + cid + ".setString(" + index + dynamic + ", String.valueOf(" + incomingConnName + "." + column.getName() + "));\r\n");
            setStmt.append("}");
        } else if(typeToGenerate.equals("Date")) {
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " != null) {\r\n");
            setStmt.append(prefix + cid + ".setTimestamp(" + index + dynamic + ", new java.sql.Timestamp(" + incomingConnName + "." + column.getName() + ".getTime()));\r\n");
            setStmt.append("} else {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.TIMESTAMP);\r\n");
            setStmt.append("}\r\n");
        } else if(typeToGenerate.equals("Bytes") && (dbType != null && (dbType.equals("LONG RAW") || dbType.equals("RAW")))) {
            setStmt.append(prefix + cid + ".setBytes(" + index + dynamic + ", " + incomingConnName + "." + column.getName() + ");\r\n");
        } else if(typeToGenerate.equals("String") && (dbType != null && dbType.equals("CLOB"))) {
            setStmt.append(prefix + cid + ".setCharacterStream(" + index + dynamic + ", new java.io.StringReader(" + incomingConnName + "." + column.getName() + "), " + incomingConnName + "." + column.getName() + ".length());\r\n");                
        } else if (typeToGenerate.equals("Bytes") && (dbType != null && dbType.equals("BLOB"))) {
            setStmt.append(prefix + cid + ".setBinaryStream(" + index + dynamic + ", new java.io.ByteArrayInputStream((byte[])" + incomingConnName + "." + column.getName() + "), ((byte[])" + incomingConnName + "." + column.getName() + ").length);\r\n");
        }
        else {
            setStmt.append(prefix + cid + ".set" + typeToGenerate + "(" + index + dynamic + ", " + incomingConnName + "." + column.getName() + ");\r\n");
        }
        if(isObject) {
            setStmt.append("}\r\n");
        }
        return setStmt.toString();            
    }
    
    public String generateSetStmt(String typeToGenerate, Column column, int index, String incomingConnName, String cid, int actionType) {
        return generateSetStmt(typeToGenerate, column, index, incomingConnName, cid, actionType, null); 
    }
    
    public String generateSetStmt(String typeToGenerate, Column column, int index, 
            String incomingConnName, String cid, int actionType, String dynamic) {
        
        if(dynamic==null) {
            dynamic="";
        } else {
            dynamic+=cid;
        }
        
        boolean isObject = false;
        String prefix = null;
        if(actionType == NORMAL_TYPE) {
            prefix = "pstmt_";
        } else if(actionType == INSERT_TYPE) {
            prefix = "pstmtInsert_";
        } else if(actionType == UPDATE_TYPE) {
            prefix = "pstmtUpdate_";
        }
        StringBuilder setStmt = new StringBuilder();
        if(typeToGenerate.equals("Character")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CHAR);\r\n");                
        } else if(typeToGenerate.equals("Date")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.DATE);\r\n");                
        } else if(typeToGenerate.equals("byte[]")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            if(column.column != null && ("BINARY".equals(column.column.getType()) || "VARBINARY".equals(column.column.getType()))) {
                setStmt.append(prefix + cid + ".setBytes(" + index + dynamic + ", null);\r\n");
            } else {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.ARRAY);\r\n");                
            }
        } else if(typeToGenerate.equals("Long") || typeToGenerate.equals("Byte") || typeToGenerate.equals("Integer") || typeToGenerate.equals("Short")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.INTEGER);\r\n");                
        } else if(typeToGenerate.equals("String")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.VARCHAR);\r\n");                
        } else if(typeToGenerate.equals("Object")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            if(column.column != null && ("BINARY".equals(column.column.getType()) || "VARBINARY".equals(column.column.getType()))) {
                setStmt.append(prefix + cid + ".setBytes(" + index + dynamic + ", null);\r\n");
            } else {
                setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.OTHER);\r\n");                
            }               
        } else if(typeToGenerate.equals("Boolean")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.BOOLEAN);\r\n");                
        } else if(typeToGenerate.equals("Double")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.DOUBLE);\r\n");                
        } else if(typeToGenerate.equals("Float")) {
            isObject = true;
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.FLOAT);\r\n");                
        }
        if(isObject) {
            setStmt.append("} else {");
        }
        typeToGenerate = getGenerateType(typeToGenerate);
        if(typeToGenerate.equals("Char") || typeToGenerate.equals("Character")) {
            if(isObject) {
                setStmt.append("if(" + incomingConnName + "." + column.getName() + " == null) {\r\n"); 
            } else {
                setStmt.append("if(String.valueOf(" + incomingConnName + "." + column.getName() + ").toLowerCase().equals(\"null\")) {\r\n");
            }
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.CHAR);\r\n");
            setStmt.append("} else if(" + incomingConnName + "." + column.getName() + " == '\0'){\r\n");
            setStmt.append(prefix + cid + ".setString(" + index + dynamic + ", \"\");\r\n");
            setStmt.append("} else {\r\n");
            setStmt.append(prefix + cid + ".setString(" + index + dynamic + ", String.valueOf(" + incomingConnName + "." + column.getName() + "));\r\n");
            setStmt.append("}");
        } else if(typeToGenerate.equals("Date")) {
            setStmt.append("if(" + incomingConnName + "." + column.getName() + " != null) {\r\n");
            setStmt.append(prefix + cid + ".setTimestamp(" + index + dynamic + ", new java.sql.Timestamp(" + incomingConnName + "." + column.getName() + ".getTime()));\r\n");
            setStmt.append("} else {\r\n");
            setStmt.append(prefix + cid + ".setNull(" + index + dynamic + ", java.sql.Types.TIMESTAMP);\r\n");
            setStmt.append("}\r\n");
        } else {
            setStmt.append(prefix + cid + ".set" + typeToGenerate + "(" + index + dynamic + ", " + incomingConnName + "." + column.getName() + ");\r\n");
        }
        if(isObject) {
            setStmt.append("}\r\n");
        }
        return setStmt.toString();
    }
    
    // @Deprecated : see bug8551
    public String retrieveSQL(String generatedType, Column column, String incomingConnName, String cid, String stmt) {
        String replaceStr = null;
        if(generatedType.equals("char") || generatedType.equals("Character")) {
            replaceStr = "\"'\" + String.valueOf(" + incomingConnName + "." + column.getName() + ") + \"'\"";
        } else if(generatedType.equals("String")) {
            replaceStr = "\"'\" + " + incomingConnName + "." + column.getName() + " + \"'\"";
        } else if(generatedType.equals("java.util.Date")) {
            replaceStr = "\"'\" + new java.text.SimpleDateFormat(" + column.getColumn().getPattern() + ").format(" + incomingConnName + "." + column.getName() + ") + \"'\"";
            
        } else {
            replaceStr = "String.valueOf(" + incomingConnName + "." + column.getName() + ")";
        }
        
        
        if (generatedType.equals("int") || generatedType.equals("float") || generatedType.equals("double") ||generatedType.equals("long") 
                || generatedType.equals("short") || generatedType.equals("boolean") || generatedType.equals("byte") || generatedType.equals("char")) {
            return stmt + cid + ".replaceFirst(\"\\\\?\", " +  replaceStr + ")";
        }else {
            return stmt + cid + ".replaceFirst(\"\\\\?\", " + incomingConnName + "." + column.getName() +"== null ?  \"null\" :" + replaceStr + ")";
        }
        
            
    }
    
    public String retrieveSQL(String generatedType, Column column, String incomingConnName, String cid, String stmt, int index, String sqlSplit) {
        return retrieveSQL(null,generatedType, column, incomingConnName, cid, stmt, index, sqlSplit);
    }
    public String retrieveSQL(String dynCol,String generatedType, Column column, String incomingConnName, String cid, String stmt, int index, String sqlSplit) {
        String replaceStr = null;
        String dynamicIndex=""+index;
        if(dynCol!=null){
            dynamicIndex = incomingConnName+"."+dynCol+".getColumnCount()+"+index;
        }
        if(generatedType.equals("char") || generatedType.equals("Character")) {
            replaceStr = "\"'\" + String.valueOf(" + incomingConnName + "." + column.getName() + ") + \"'\"";
        } else if(generatedType.equals("String")) {
            replaceStr = "\"'\" + " + incomingConnName + "." + column.getName() + " + \"'\"";
        } else if(generatedType.equals("java.util.Date")) {
            replaceStr = "\"'\" + new java.text.SimpleDateFormat(" + column.getColumn().getPattern() + ").format(" + incomingConnName + "." + column.getName() + ") + \"'\"";
            
        } else {
            replaceStr = "String.valueOf(" + incomingConnName + "." + column.getName() + ")";
        }
        
        if (generatedType.equals("int") || generatedType.equals("float") || generatedType.equals("double") ||generatedType.equals("long") 
                || generatedType.equals("short") || generatedType.equals("boolean") || generatedType.equals("byte") || generatedType.equals("char")) {
                if (index == 1 && dynCol == null) {
                    return  stmt + cid + ".append("+sqlSplit+cid+"[0]).append(" +  replaceStr + ").append("+sqlSplit+cid+"["+index+"])";
                } else {
                    return  stmt + cid + ".append(" +  replaceStr + ").append("+sqlSplit+cid+"["+dynamicIndex+"])";
                }
        }else {
            if (index == 1 && dynCol == null) {
                return stmt + cid + ".append("+sqlSplit+cid+"[0]).append(" + incomingConnName + "." + column.getName() +"== null ?  \"null\" :" + replaceStr + ").append("+sqlSplit+cid+"["+index+"])";
            } else {
                return stmt + cid + ".append(" + incomingConnName + "." + column.getName() +"== null ?  \"null\" :" + replaceStr + ").append("+sqlSplit+cid+"["+dynamicIndex+"])";
            }
        }
    }
    
    //feature:2880 @6980 in debug mode
    public String retrieveSQL(String generatedType, Column column, String incomingConnName, String cid, String stmt, int index, String sqlSplit, String replaceFixedStr) {
        return retrieveSQL(null,generatedType, column, incomingConnName, cid, stmt, index, sqlSplit, replaceFixedStr);
    }
    
    public String retrieveSQL(String dynCol,String generatedType, Column column, String incomingConnName, String cid, String stmt, int index, String sqlSplit, String replaceFixedStr) {
        String dynamicIndex=""+index;
        if(dynCol!=null){
            dynamicIndex = incomingConnName+"."+dynCol+".getColumnCount()+"+index;
        }
        if (index == 1 && dynCol == null) {
            return stmt + cid + ".append("+sqlSplit+cid+"[0]).append(" +replaceFixedStr+ ").append("+sqlSplit+cid+"["+index+"])";
        } else {
            return stmt + cid + ".append(" +replaceFixedStr+").append("+sqlSplit+cid+"["+dynamicIndex+"])";
        }
    }
    
    //extract a method for feature:2880, it is a default implement, need every tDBOutput to implement it, if it really want to support the "whereNULL" issue
    //@see: the implement of MSSQLManager
    //feature:2880 @6980
    //i.e: 
    //1.select * FROM user where ((true = true AND name is NULL) OR name = ?);
    //2.select * FROM user where ((true = false AND name is NULL) OR name = ?);
    
    public void whereStmtSupportNull(StringBuilder updateWhereStmt, Column column) {
        boolean whereSupportNull = false;

        //if node = null, it means some components have not support the "whereNULL" issue yet.
        if (node != null) {
            whereSupportNull = ElementParameterParser.getValue(node, "__SUPPORT_NULL_WHERE__").equals("true");
        }
        if (whereSupportNull && column.getColumn()!=null && column.getColumn().isNullable()) {
            updateWhereStmt.append("((" + getLProtectedChar(column.getColumnName()) + column.getColumnName() + getRProtectedChar(column.getColumnName())
                    + " IS NULL AND " + getColumnIsNullCondition() +" " + column.getOperator() + " " + column.getSqlStmt() + ") ");
            updateWhereStmt.append("OR " + getLProtectedChar(column.getColumnName()) + column.getColumnName() + getRProtectedChar(column.getColumnName())
                    + column.getOperator() + column.getSqlStmt() + ")");
        } else {
            //if node = null, go this branch as the old behave
            updateWhereStmt.append(getLProtectedChar(column.getColumnName()) + column.getColumnName() + getRProtectedChar(column.getColumnName()) + " "
                    + column.getOperator() + " " + column.getSqlStmt());
        }
    }
    
    //feature:2880 @6980
    //need all db to implement, return the column whether is null(in java) prefix condition.
    protected String getColumnIsNullCondition() {
        return "1";
    }
    
    // for feature:2880, it will consider the "whereNULL" issue in the generated code.
    // @Deprecated: because the "index" with a small problem when increase it.
    private String generateSetStmt_4_whereSupportNull(String typeToGenerate, Column column, int index,
            String incomingConnName, String cid, int actionType) {

        boolean whereSupportNull = false;

        // if node = null, it means some components have not support the "whereNULL" issue yet.
        if (node != null) {
            whereSupportNull = ElementParameterParser.getValue(node, "__SUPPORT_NULL_WHERE__").equals("true");
        }

        StringBuilder setStmt = new StringBuilder();

        // the 3 conditions are important
        if (column.isUpdateKey() && whereSupportNull && column.getColumn().isNullable()) {
            setStmt.append(generateSetBooleanForNullableKeyStmt(column, index, incomingConnName, cid, NORMAL_TYPE));
        }

        // the old behave
        setStmt.append(generateSetStmt(typeToGenerate, column, index, incomingConnName, cid, NORMAL_TYPE));

        return setStmt.toString();
    }

    protected String generateSetBooleanForNullableKeyStmt(String dynCol,Column column, int index, String incomingConnName, String cid,
            int actionType) {
        String dynamicIndex=""+index;
        if(dynCol!=null){
            dynamicIndex = incomingConnName+"."+dynCol+".getColumnCount()+"+index;
        }
        String prefix = null;
        if (actionType == NORMAL_TYPE) {
            prefix = "pstmt_";
        } else if (actionType == INSERT_TYPE) {
            prefix = "pstmtInsert_";
        } else if (actionType == UPDATE_TYPE) {
            prefix = "pstmtUpdate_";
        }
        StringBuilder setStmt = new StringBuilder();
        //TODO generate setXXXX code according to each db .
        setStmt.append(prefix + cid + ".setInt(" + dynamicIndex + ",  ((" + incomingConnName + "." + column.getName() + "==null)?1:0));\r\n");
        return setStmt.toString();
    } 
    
    // for feature:2880 @6980, generate the "set XXXX" java code.thie code is about  the column whether is null(in java) prefix condition.
    //@see:getColumnIsNullCondition()
    //need all db to implement.
    protected String generateSetBooleanForNullableKeyStmt(Column column, int index, String incomingConnName, String cid,
            int actionType) {
        return generateSetBooleanForNullableKeyStmt(null, column, index, incomingConnName, cid, actionType);
    }     
    
    public String getCopyFromCSVSQL(List<IMetadataColumn> columnList, 
            String fieldDelimiter, 
            String newLineChar,
            String nullIndicator) {
    return null;
        
    }
    
    public String generateCode4TabelExist() {
        boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
        String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
        StringBuilder code = new StringBuilder();  
        code.append("String tableNameForSearch_" + cid + "= " + getTableName4Search(useExistingConnection, connection) + ";\r\n");
        if (hasSchema()) {
            code.append("String dbschemaForSearch_" + cid + "= null;\r\n");
            code.append("if(dbschema_" + cid + "== null || dbschema_" + cid + ".trim().length() == 0) {\r\n");
            code.append("dbschemaForSearch_" + cid + "= " + getUserName4Search(useExistingConnection, connection)+ ";\r\n");
            code.append("} else {\r\n");
            code.append("dbschemaForSearch_" + cid + "= " + getShemaName4Search(useExistingConnection, connection)+ ";\r\n");
            code.append("}\r\n");
        }
        return code.toString();
    }
    
    protected String getTableName4Search(boolean useExistingConnection, String connection) {
        return "\""+getLProtectedChar()+ "\" + " + ElementParameterParser.getValue(node,"__TABLE__") +" + \"" + getRProtectedChar() + "\""; 
    }
    
    protected String getUserName4Search(boolean useExistingConnection, String connection) {
        return "";   
    }

    /*
     * maybe some db need add getLProtectedChar() and getRProtectedChar() to schema name.
     * this because of some db default add getLProtectedChar() and getRProtectedChar() to schema when create table. e.g:db2
     * 
     * in fact the db add getLProtectedChar() and getRProtectedChar() to scheam when create table that is wrong
    */
    protected String getShemaName4Search(boolean useExistingConnection, String connection) {
        return "";   
    }   
    
    protected boolean hasSchema() {
        return false;
    }
    
}
