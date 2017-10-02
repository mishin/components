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
package org.talend.components.google.drive.runtime;

import static java.net.InetSocketAddress.createUnresolved;
import static java.net.Proxy.Type.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.google.drive.GoogleDriveProvideConnectionProperties;
import org.talend.components.google.drive.GoogleDriveProvideRuntime;
import org.talend.components.google.drive.connection.GoogleDriveConnectionProperties;
import org.talend.components.google.drive.runtime.client.GoogleDriveCredentialWithAccessToken;
import org.talend.components.google.drive.runtime.client.GoogleDriveCredentialWithInstalledApplication;
import org.talend.components.google.drive.runtime.client.GoogleDriveCredentialWithServiceAccount;
import org.talend.components.google.drive.runtime.client.GoogleDriveService;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.ValidationResultMutable;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport.Builder;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.User;

public class GoogleDriveRuntime extends GoogleDriveValidator
        implements RuntimableRuntime<ComponentProperties>, GoogleDriveProvideConnectionProperties, GoogleDriveProvideRuntime {

    private static final String APPLICATION_SUFFIX_GPN_TALEND = " (GPN:Talend)";

    private Drive service;

    private GoogleDriveUtils utils;

    private NetHttpTransport httpTransport;

    protected GoogleDriveProvideConnectionProperties properties;

    private transient static final Logger LOG = LoggerFactory.getLogger(GoogleDriveRuntime.class);

    protected static final I18nMessages messages = GlobalI18N.getI18nMessageProvider()
            .getI18nMessages(GoogleDriveSourceOrSink.class);

    @Override
    public ValidationResult initialize(RuntimeContainer container, ComponentProperties properties) {
        this.properties = (GoogleDriveProvideConnectionProperties) properties;

        return validateConnectionProperties(getConnectionProperties());
    }

    @Override
    public GoogleDriveConnectionProperties getConnectionProperties() {
        return properties.getConnectionProperties().getEffectiveConnectionProperties();
    }

    public GoogleDriveProvideConnectionProperties getProperties() {
        return properties;
    }

    public NetHttpTransport getHttpTransport() throws GeneralSecurityException, IOException {
        if (httpTransport == null) {
            GoogleDriveConnectionProperties conn = getConnectionProperties();
            if (conn.useSSL.getValue() || conn.useProxy.getValue()) {
                Builder tmpBuilder = new NetHttpTransport.Builder();
                if (conn.useProxy.getValue()) {
                    Proxy proxy = new Proxy(HTTP, createUnresolved(conn.proxyHost.getValue(), conn.proxyPort.getValue()));
                    tmpBuilder.setProxy(proxy);
                }
                if (conn.useSSL.getValue()) {
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    FileInputStream fi = new FileInputStream(conn.sslTrustStore.getValue());
                    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                    ks.load(fi, conn.sslTrustStorePassword.getValue().toCharArray());
                    fi.close();
                    tmf.init(ks);
                    SSLContext sslContext = SSLContext.getInstance(conn.sslAlgorithm.getValue());
                    sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
                    tmpBuilder.setSslSocketFactory(sslContext.getSocketFactory());
                }
                httpTransport = tmpBuilder.build();
            } else {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            }
        }

        return httpTransport;
    }

    public String getApplicationName() {
        String appName = getConnectionProperties().applicationName.getValue();
        if (!appName.endsWith(APPLICATION_SUFFIX_GPN_TALEND)) {
            appName = appName + APPLICATION_SUFFIX_GPN_TALEND;
        }
        return appName;
    }

    private Credential getCredential(NetHttpTransport httpTransport) throws IOException, GeneralSecurityException {
        GoogleDriveConnectionProperties conn = getConnectionProperties();
        /* get rid of warning on windows until fixed... https://github.com/google/google-http-java-client/issues/315 */
        final java.util.logging.Logger dsLogger = java.util.logging.Logger.getLogger(FileDataStoreFactory.class.getName());
        dsLogger.setLevel(java.util.logging.Level.SEVERE);
        File dataStore;
        switch (conn.oAuthMethod.getValue()) {
        case AccessToken:
            return GoogleDriveCredentialWithAccessToken.builder().accessToken(conn.accessToken.getValue()).build();
        case InstalledApplicationWithIdAndSecret:
            dataStore = new File(conn.datastorePath.getValue());
            return GoogleDriveCredentialWithInstalledApplication.builderWithIdAndSecret(httpTransport, dataStore)
                    .clientId(conn.clientId.getValue()).clientSecret(conn.clientSecret.getValue()).build();
        case InstalledApplicationWithJSON:
            dataStore = new File(conn.datastorePath.getValue());
            return GoogleDriveCredentialWithInstalledApplication.builderWithJSON(httpTransport, dataStore)
                    .clientSecretFile(new File(conn.clientSecretFile.getValue())).build();
        case ServiceAccount:
            return GoogleDriveCredentialWithServiceAccount.builder()
                    .serviceAccountJSONFile(new File(conn.serviceAccountFile.getValue())).build();
        }
        throw new IllegalArgumentException(messages.getMessage("error.credential.oaut.method"));
    }

    public Drive getDriveService() throws GeneralSecurityException, IOException {
        if (service == null) {
            service = new GoogleDriveService(getApplicationName(), getHttpTransport(), getCredential(getHttpTransport()))
                    .getDriveService();
        }
        return service;
    }

    public GoogleDriveUtils getDriveUtils() throws GeneralSecurityException, IOException {
        if (utils == null) {
            utils = new GoogleDriveUtils(getDriveService());
        }
        return utils;
    }

    public ValidationResult validateConnection(GoogleDriveConnectionProperties connectionProperties) {
        ValidationResultMutable vr = new ValidationResultMutable(validateConnectionProperties(connectionProperties));
        if (Result.ERROR.equals(vr.getStatus())) {
            return vr;
        }
        try {
            // make a dummy call to check drive's connection..
            User u = getDriveService().about().get().setFields("user").execute().getUser();
            LOG.debug("[validateConnection] Testing User Properties: {}", u);
        } catch (Exception ex) {
            LOG.error("[validateConnection] " + ex);
            vr.setStatus(Result.ERROR);
            vr.setMessage(ex.getMessage());
            return vr;
        }
        vr.setStatus(Result.OK);
        vr.setMessage(messages.getMessage("message.connectionSuccessful"));
        return vr;
    }
}
