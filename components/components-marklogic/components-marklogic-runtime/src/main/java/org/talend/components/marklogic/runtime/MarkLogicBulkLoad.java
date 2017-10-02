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
package org.talend.components.marklogic.runtime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.ComponentDriverInitialization;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.marklogic.tmarklogicbulkload.MarkLogicBulkLoadProperties;
import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.components.marklogic.util.CommandExecutor;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class MarkLogicBulkLoad implements ComponentDriverInitialization {

    private static final I18nMessages MESSAGES = GlobalI18N.getI18nMessageProvider().getI18nMessages(MarkLogicBulkLoad.class);

    private transient static final Logger LOGGER = LoggerFactory.getLogger(MarkLogicBulkLoad.class);

    private MarkLogicBulkLoadProperties bulkLoadProperties;

    @Override
    public void runAtDriver(RuntimeContainer container) {
        String mlcpCommand = prepareMlcpCommand();

        LOGGER.debug(MESSAGES.getMessage("messages.debug.command", mlcpCommand));
        LOGGER.info(MESSAGES.getMessage("messages.info.startBulkLoad"));
        try {
            Process mlcpProcess = CommandExecutor.executeCommand(mlcpCommand);
            mlcpProcess.waitFor();
            try (InputStream normalInput = mlcpProcess.getInputStream();
                    BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(normalInput));
                    InputStream errorInput = mlcpProcess.getErrorStream();
                    BufferedReader errorReader = new BufferedReader(new java.io.InputStreamReader(errorInput))) {

                while (reader.ready()) {
                    System.out.println(reader.readLine());
                }
                while (errorReader.ready()) {
                    System.err.println(errorReader.readLine());
                }

                LOGGER.info(MESSAGES.getMessage("messages.info.finishBulkLoad"));
            } catch (IOException e) {
                LOGGER.error(MESSAGES.getMessage("messages.error.ioexception", e.getMessage()));
                throw new ComponentException(e);
            }

        } catch (Exception e) {
            LOGGER.error(MESSAGES.getMessage("messages.error.exception", e.getMessage()));
            throw new ComponentException(e);
        }
    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, Properties properties) {
        ValidationResultMutable validationResult = new ValidationResultMutable();
        if (properties instanceof MarkLogicBulkLoadProperties) {
            bulkLoadProperties = (MarkLogicBulkLoadProperties) properties;

            boolean isRequiredPropertiesMissed = isRequiredPropertiesMissed();
            if (isRequiredPropertiesMissed) {
                validationResult.setStatus(ValidationResult.Result.ERROR);
                validationResult.setMessage(MESSAGES.getMessage("error.missedProperties"));
            }

        } else {
            validationResult.setStatus(ValidationResult.Result.ERROR);
            validationResult.setMessage(MESSAGES.getMessage("error.wrongProperties"));
        }

        return validationResult;
    }

    private boolean isRequiredPropertiesMissed() {
        MarkLogicConnectionProperties connection = bulkLoadProperties.connection;
        boolean isRequiredPropertiesMissed = false;
        if (connection.isReferencedConnectionUsed()) {
            MarkLogicConnectionProperties referencedConnection = bulkLoadProperties.connection.referencedComponent.getReference();

            isRequiredPropertiesMissed = referencedConnection.host.getStringValue().isEmpty()
                    || referencedConnection.port.getValue() == null || referencedConnection.database.getStringValue().isEmpty()
                    || referencedConnection.username.getStringValue().isEmpty() || referencedConnection.password.getStringValue().isEmpty();
        } else {
            isRequiredPropertiesMissed = connection.host.getStringValue().isEmpty()
                    || connection.port.getValue() == null || connection.database.getStringValue().isEmpty()
                    || connection.username.getStringValue().isEmpty() || connection.password.getStringValue().isEmpty();
        }

        return isRequiredPropertiesMissed || bulkLoadProperties.loadFolder.getStringValue().isEmpty();
    }


    String prepareMlcpCommand() {
        StringBuilder mlcpCommand = new StringBuilder();

        MarkLogicConnectionProperties connection = bulkLoadProperties.connection;
        boolean useExistingConnection = connection.isReferencedConnectionUsed();
        //connection properties could be also taken from referencedComponent
        String userName = useExistingConnection ?
                connection.referencedComponent.getReference().username.getStringValue() :
                connection.username.getStringValue();
        String password = useExistingConnection ?
                connection.referencedComponent.getReference().password.getStringValue() :
                connection.password.getStringValue();
        String host = useExistingConnection ?
                connection.referencedComponent.getReference().host.getStringValue() :
                connection.host.getStringValue();
        Integer port = useExistingConnection ?
                connection.referencedComponent.getReference().port.getValue() :
                connection.port.getValue();
        String database = useExistingConnection ?
                connection.referencedComponent.getReference().database.getStringValue() :
                connection.database.getStringValue();

        //need to process load folder value (it should start from '/' and has only unix file-separators)
        String loadPath = bulkLoadProperties.loadFolder.getStringValue();
        if(loadPath.contains(":")){
            loadPath = "/" + loadPath;
        }
        loadPath = (loadPath.replaceAll("\\\\","/"));

        String prefix = bulkLoadProperties.docidPrefix.getStringValue();
        if(prefix != null && (prefix.endsWith("/") || prefix.endsWith("\\"))){
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        String additionalMLCPParameters = bulkLoadProperties.mlcpParams.getStringValue();

        mlcpCommand.append(prepareMlcpCommandStart(System.getProperty("os.name")));
        mlcpCommand.append("import ");
        mlcpCommand.append("-username ").append(userName).append(" ");
        mlcpCommand.append("-password ").append(password).append(" ");
        mlcpCommand.append("-host ").append(host).append(" ");
        mlcpCommand.append("-port ").append(port).append(" ");
        if (StringUtils.isNotEmpty(database) && !("\"\"".equals(database))) {
            mlcpCommand.append("-database ").append(database).append(" ");
        }
        mlcpCommand.append("-input_file_path ").append(loadPath)
                .append(" ");
        if (!StringUtils.isEmpty(prefix)) {
            mlcpCommand.append("-output_uri_replace \"")
                    .append(loadPath)
                    .append(",'")
                    .append(prefix)
                    .append("'\"");
        }
        if (StringUtils.isNotEmpty(additionalMLCPParameters) && !(("\"\"".equals(additionalMLCPParameters)))) {
            mlcpCommand.append(" ");
            mlcpCommand.append(additionalMLCPParameters);
        }

        return mlcpCommand.toString();
    }

    String prepareMlcpCommandStart(String osName) {
        if (osName.toLowerCase().startsWith("windows")) {
            return "cmd /c mlcp.bat ";
        } else {
            return "mlcp.sh ";
        }
    }
}
