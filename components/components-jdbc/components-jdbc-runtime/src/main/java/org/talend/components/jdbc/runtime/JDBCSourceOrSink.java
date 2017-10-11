
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
package org.talend.components.jdbc.runtime;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.common.avro.JDBCAvroRegistry;
import org.talend.components.common.avro.JDBCResultSetIndexedRecordConverter;
import org.talend.components.common.avro.JDBCTableMetadata;
import org.talend.components.common.dataset.DatasetProperties;
import org.talend.components.common.datastore.DatastoreProperties;
import org.talend.components.jdbc.ComponentConstants;
import org.talend.components.jdbc.JdbcComponentErrorsCode;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.avro.JDBCAvroRegistryString;
import org.talend.components.jdbc.avro.ResultSetStringRecordConverter;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.components.jdbc.runtime.setting.JdbcRuntimeSourceOrSinkDefault;
import org.talend.components.jdbc.runtime.setting.ModuleMetadata;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.avro.AvroRegistry;
import org.talend.daikon.avro.converter.IndexedRecordConverter;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.ValidationResult;

/**
 * common JDBC runtime execution object
 *
 */
public class JDBCSourceOrSink extends JdbcRuntimeSourceOrSinkDefault {

    private static final long serialVersionUID = 1L;

    public RuntimeSettingProvider properties;

    protected AllSetting setting;

    private transient AvroRegistry avroRegistry;

    private transient IndexedRecordConverter<ResultSet, IndexedRecord> converter;

    @Override
    public ValidationResult initialize(RuntimeContainer runtime, ComponentProperties properties) {
        this.properties = (RuntimeSettingProvider) properties;
        setting = this.properties.getRuntimeSetting();

        // TODO use another registry which use the db mapping files
        avroRegistry = JDBCAvroRegistry.get();
        converter = new JDBCResultSetIndexedRecordConverter();
        ((JDBCResultSetIndexedRecordConverter) converter).setInfluencer(setting);

        return ValidationResult.OK;
    }

    // TODO adjust it, now only as a temp workaround for reuse current class in the datastore runtime
    public ValidationResult initialize(RuntimeContainer runtime, DatastoreProperties properties) {
        this.properties = (RuntimeSettingProvider) properties;
        setting = this.properties.getRuntimeSetting();

        avroRegistry = JDBCAvroRegistryString.get();
        converter = new ResultSetStringRecordConverter();

        return ValidationResult.OK;
    }

    // TODO adjust it, now only as a temp workaround for reuse current class in the dataset runtime
    @SuppressWarnings("rawtypes")
    public ValidationResult initialize(RuntimeContainer runtime, DatasetProperties properties) {
        this.properties = (RuntimeSettingProvider) properties;
        setting = this.properties.getRuntimeSetting();

        avroRegistry = JDBCAvroRegistryString.get();
        converter = new ResultSetStringRecordConverter();

        return ValidationResult.OK;
    }

    @Override
    public ValidationResult validate(RuntimeContainer runtime) {
        return JdbcRuntimeUtils.validate(runtime, this);
    }

    // TODO not sure we need this method for JDBC as :
    // this is only a flat table list, but in fact for database, it's a tree, not list, so this method is not so good
    // tup team consider to use the old way(if that, we may need to adjust JDBCTableSelectionModule which use this method) for the
    // tables show, so this method may not be useful now.
    // so no need to waste time to adjust this method now
    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer runtime) throws IOException {
        List<NamedThing> result = new ArrayList<>();
        try (Connection conn = connect(runtime);
                // TODO support all table types, not only TABLE, also VIEW, SYNONYM and so on
                ResultSet resultset = conn.getMetaData().getTables(null, null, null, new String[] { "TABLE" })) {
            while (resultset.next()) {
                String tablename = resultset.getString("TABLE_NAME");
                result.add(new SimpleNamedThing(tablename, tablename));
            }
        } catch (Exception e) {
            throw new ComponentException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e,
                    ExceptionContext.withBuilder().put("message", e.getMessage()).build());
        }
        return result;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer runtime, String tableName) throws IOException {
        try (Connection conn = connect(runtime)) {
            JDBCTableMetadata tableMetadata = new JDBCTableMetadata();
            tableMetadata.setDatabaseMetaData(conn.getMetaData()).setTablename(tableName);
            return avroRegistry.inferSchema(tableMetadata);
        } catch (Exception e) {
            throw new ComponentException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e,
                    ExceptionContext.withBuilder().put("message", e.getMessage()).build());
        }
    }

    public Schema getSchemaFromQuery(RuntimeContainer runtime, String query) {
        try (Connection conn = connect(runtime);
                Statement statement = conn.createStatement();
                ResultSet resultset = statement.executeQuery(query)) {
            ResultSetMetaData metadata = resultset.getMetaData();
            return avroRegistry.inferSchema(metadata);
        } catch (SQLSyntaxErrorException sqlSyntaxException) {
            throw new ComponentException(JdbcComponentErrorsCode.SQL_SYNTAX_ERROR, sqlSyntaxException);
        } catch (SQLException e) {
            throw new ComponentException(JdbcComponentErrorsCode.SQL_ERROR, e);
        } catch (ClassNotFoundException e) {
            throw new ComponentException(JdbcComponentErrorsCode.DRIVER_NOT_PRESENT_ERROR, e);
        }
    }

    public Connection connect(RuntimeContainer runtime) throws ClassNotFoundException, SQLException {
        // TODO now we use routines.system.TalendDataSource to get the data connection from the ESB runtime, but now we
        // can't
        // refer it by the new framework, so will fix it later

        // TODO routines.system.SharedDBConnectionLog4j, the same with the TODO above

        AllSetting setting = properties.getRuntimeSetting();

        // connection component
        Connection conn = JdbcRuntimeUtils.createConnection(setting);
        conn.setReadOnly(setting.isReadOnly());

        if (setting.getUseAutoCommit()) {
            conn.setAutoCommit(setting.getAutocommit());
        }

        if (runtime != null) {
            // if you check the api, you will find the parameter is set to the wrong location, but it's right now, as we need to
            // keep the connection component can work with some old javajet components
            runtime.setComponentData(ComponentConstants.CONNECTION_KEY, runtime.getCurrentComponentId(), conn);
            runtime.setComponentData(ComponentConstants.URL_KEY, runtime.getCurrentComponentId(), setting.getJdbcUrl());
            runtime.setComponentData(ComponentConstants.USERNAME_KEY, runtime.getCurrentComponentId(), setting.getUsername());
        }

        return conn;
    }

    public AvroRegistry getAvroRegistry() {
        return avroRegistry;
    }

    public IndexedRecordConverter<ResultSet, IndexedRecord> getConverter() {
        return converter;
    }

    // as studio will do schema list retrieve by the old way, now the method is not useful.
    // work for the wizard : catalog show, TODO make it common
    public List<String> getDBCatalogs(RuntimeContainer runtime) throws ClassNotFoundException, SQLException {
        List<String> catalogs = new ArrayList<>();

        try (Connection conn = connect(runtime); ResultSet result = conn.getMetaData().getCatalogs()) {
            if (result == null) {
                return catalogs;
            }

            while (result.next()) {
                String catalog = result.getString("TABLE_CAT");
                if (catalog != null) {
                    catalogs.add(catalog);
                }
            }
        }

        return catalogs;
    }

    // as studio will do schema list retrieve by the old way, now the method is not useful.
    // work for the wizard : schema show after catalog TODO make it common
    public List<String> getDBSchemas(RuntimeContainer runtime, String catalog) throws ClassNotFoundException, SQLException {
        List<String> dbschemas = new ArrayList<>();

        try (Connection conn = connect(runtime); ResultSet result = conn.getMetaData().getSchemas()) {
            if (result == null) {
                return dbschemas;
            }

            while (result.next()) {
                String dbschema = result.getString("TABLE_SCHEM");
                if (dbschema == null) {
                    continue;
                }

                // filter by catalog name
                if (catalog != null) {
                    String catalogname = result.getString("TABLE_CATALOG");
                    if (catalog.equals(catalogname)) {
                        dbschemas.add(dbschema);
                    }
                } else {
                    dbschemas.add(dbschema);
                }
            }
        }

        return dbschemas;
    }

    // as studio will do schema list retrieve by the old way, now the method is not useful.
    // work for the wizard : table show after schema after catalog TODO make it common
    public List<ModuleMetadata> getDBTables(RuntimeContainer runtime, String catalog, String dbschema, String tableNamePattern,
            String[] tableTypes) throws ClassNotFoundException, SQLException {
        List<ModuleMetadata> tables = new ArrayList<>();

        try (Connection conn = connect(runtime);
                ResultSet result = conn.getMetaData().getTables(catalog, dbschema, tableNamePattern, tableTypes)) {
            if (result == null) {
                return tables;
            }

            while (result.next()) {
                String table = result.getString("TABLE_NAME");

                if (table == null) {
                    continue;
                }

                String type = result.getString("TABLE_TYPE");
                String comment = result.getString("REMARKS");

                String dbcatalog = result.getString("TABLE_CAT");
                String db_schema = result.getString("TABLE_SCHEM");

                tables.add(new ModuleMetadata(dbcatalog, db_schema, table, type, comment, null));
            }
        }

        return tables;
    }

    // as studio will do schema list retrieve by the old way, now the method is not useful.
    // work for the schemas store after click finish button TODO make it common
    public List<ModuleMetadata> getDBTables(RuntimeContainer runtime, ModuleMetadata tableid)
            throws ClassNotFoundException, SQLException {
        List<ModuleMetadata> tables = new ArrayList<>();

        try (Connection conn = connect(runtime);
                ResultSet result = conn.getMetaData().getTables(tableid.catalog, tableid.dbschema, tableid.name,
                        tableid.type == null ? null : new String[] { tableid.type })) {
            if (result == null) {
                return tables;
            }

            while (result.next()) {
                String table = result.getString("TABLE_NAME");

                if (table == null) {
                    continue;
                }

                String type = result.getString("TABLE_TYPE");
                String comment = result.getString("REMARKS");

                String dbcatalog = result.getString("TABLE_CAT");
                String db_schema = result.getString("TABLE_SCHEM");

                JDBCTableMetadata tableMetadata = new JDBCTableMetadata();
                tableMetadata.setDatabaseMetaData(conn.getMetaData()).setTablename(tableid.name);
                Schema schema = avroRegistry.inferSchema(tableMetadata);

                // as ModuleMetadata is invisible for TUP, so store the metadata information to schema for TUP team can work on it
                // use the same key with JDBC api
                if (dbcatalog != null)
                    schema.addProp("TABLE_CAT", dbcatalog);

                if (db_schema != null)
                    schema.addProp("TABLE_SCHEM", db_schema);

                if (table != null)
                    schema.addProp("TABLE_NAME", table);

                if (type != null)
                    schema.addProp("TABLE_TYPE", type);

                if (comment != null)
                    schema.addProp("REMARKS", comment);

                tables.add(new ModuleMetadata(dbcatalog, db_schema, table, type, comment, schema));
            }
        }

        return tables;
    }

}
