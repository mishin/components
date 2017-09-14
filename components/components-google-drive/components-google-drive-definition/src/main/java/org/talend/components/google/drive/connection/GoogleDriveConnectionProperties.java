package org.talend.components.google.drive.connection;

import static org.talend.components.google.drive.GoogleDriveComponentDefinition.SOURCE_OR_SINK_CLASS;
import static org.talend.components.google.drive.GoogleDriveComponentDefinition.getSandboxedInstance;
import static org.talend.components.google.drive.connection.GoogleDriveConnectionProperties.OAuthMethod.InstalledApplicationWithIdAndSecret;
import static org.talend.components.google.drive.connection.GoogleDriveConnectionProperties.OAuthMethod.InstalledApplicationWithJSON;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.nio.file.Paths;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.properties.ComponentPropertiesImpl;
import org.talend.components.api.properties.ComponentReferenceProperties;
import org.talend.components.google.drive.GoogleDriveProvideConnectionProperties;
import org.talend.components.google.drive.GoogleDriveRuntime;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResultMutable;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.sandbox.SandboxedInstance;

public class GoogleDriveConnectionProperties extends ComponentPropertiesImpl implements GoogleDriveProvideConnectionProperties {

    private transient static final Logger LOG = LoggerFactory.getLogger(GoogleDriveConnectionProperties.class);

    public static final String FORM_WIZARD = "Wizard";

    public Property<String> name = newString("name").setRequired();

    public Property<String> applicationName = newString("applicationName").setRequired();

    public enum OAuthMethod {
        AccessToken,
        InstalledApplicationWithIdAndSecret,
        InstalledApplicationWithJSON,
        ServiceAccount
    }

    public Property<OAuthMethod> oAuthMethod = newEnum("oAuthMethod", OAuthMethod.class);

    public Property<String> accessToken = newString("accessToken").setRequired();

    public Property<String> clientId = newString("clientId").setRequired();

    public Property<String> clientSecret = newString("clientSecret").setRequired().setFlags(
            EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property<String> clientSecretFile = newString("clientSecretFile");

    public Property<String> serviceAccountFile = newString("serviceAccountFile");

    // Proxy
    public Property<Boolean> useProxy = newBoolean("useProxy");

    public Property<String> proxyHost = newString("proxyHost");

    public Property<Integer> proxyPort = newInteger("proxyPort");

    // SSL
    public Property<Boolean> useSSL = newBoolean("useSSL");

    public Property<String> sslAlgorithm = newString("sslAlgorithm");

    public Property<String> sslTrustStore = newString("sslTrustStore");

    public Property<String> sslTrustStorePassword = newString("sslTrustStorePassword").setFlags(
            EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    // datastore to persist the credential's access token and/or refresh token.

    public Property<String> datastorePath = newString("datastorePath");

    //
    public PresentationItem testConnection = new PresentationItem("testConnection", "Test connection");

    @SuppressWarnings("unchecked")
    public final ComponentReferenceProperties<GoogleDriveConnectionProperties> referencedComponent = new ComponentReferenceProperties(
            "referencedComponent", GoogleDriveConnectionDefinition.COMPONENT_NAME);

    public GoogleDriveConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();

        //
        useProxy.setValue(false);
        proxyHost.setValue("127.0.0.1");
        proxyPort.setValue(8087);
        //
        useSSL.setValue(false);
        sslAlgorithm.setValue("SSL");
        //
        applicationName.setValue("");
        oAuthMethod.setPossibleValues(OAuthMethod.values());
        oAuthMethod.setValue(OAuthMethod.AccessToken);
        clientId.setValue("");
        clientSecret.setValue("");
        clientSecretFile.setValue("");
        serviceAccountFile.setValue("");
        //
        String dsPath = ".credentials/google-drive-talend";
        try {
            dsPath = Paths.get(System.getProperty("user.home"), ".credentials/google-drive-talend").toAbsolutePath().toString();
            dsPath = dsPath.replace("\\", "/");
        } catch (Exception e) {
            // env no set
        }
        datastorePath.setValue(dsPath);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = Form.create(this, Form.MAIN);
        mainForm.addRow(applicationName);
        mainForm.addRow(oAuthMethod);
        mainForm.addRow(accessToken);
        mainForm.addRow(clientId);
        mainForm.addColumn(widget(clientSecret).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        mainForm.addRow(widget(clientSecretFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addRow(widget(serviceAccountFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        //
        mainForm.addRow(useProxy);
        mainForm.addRow(proxyHost);
        mainForm.addColumn(proxyPort);
        //
        mainForm.addRow(useSSL);
        mainForm.addRow(sslAlgorithm);
        mainForm.addColumn(widget(sslTrustStore).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addColumn(widget(sslTrustStorePassword).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        // Advanced form
        Form advancedForm = Form.create(this, Form.ADVANCED);
        advancedForm.addRow(widget(datastorePath).setWidgetType(Widget.DIRECTORY_WIDGET_TYPE));
        // A form for a reference to a connection
        Form refForm = Form.create(this, Form.REFERENCE);
        Widget compListWidget = widget(referencedComponent).setWidgetType(Widget.COMPONENT_REFERENCE_WIDGET_TYPE);
        refForm.addRow(compListWidget);
        refForm.addRow(mainForm);
        // Wizard
        Form wizardForm = Form.create(this, FORM_WIZARD);
        wizardForm.addRow(applicationName);
        wizardForm.addRow(oAuthMethod);
        wizardForm.addRow(accessToken);
        wizardForm.addRow(clientId);
        wizardForm.addColumn(widget(clientSecret).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        wizardForm.addRow(widget(clientSecretFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        wizardForm.addRow(widget(serviceAccountFile).setWidgetType(Widget.FILE_WIDGET_TYPE));
        //
        wizardForm.addRow(useProxy);
        wizardForm.addRow(proxyHost);
        wizardForm.addColumn(proxyPort);
        //
        wizardForm.addRow(useSSL);
        wizardForm.addRow(sslAlgorithm);
        wizardForm.addColumn(widget(sslTrustStore).setWidgetType(Widget.FILE_WIDGET_TYPE));
        wizardForm.addColumn(widget(sslTrustStorePassword).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        //
        wizardForm.addRow(widget(testConnection).setLongRunning(true).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);

        String refComponentIdValue = getReferencedComponentId();
        boolean useOtherConnection = refComponentIdValue != null
                && refComponentIdValue.startsWith(GoogleDriveConnectionDefinition.COMPONENT_NAME);
        if (form.getName().equals(Form.MAIN) || form.getName().equals(FORM_WIZARD)) {
            // hides everything...
            form.getWidget(applicationName.getName()).setHidden(true);
            form.getWidget(oAuthMethod.getName()).setHidden(true);
            form.getWidget(accessToken.getName()).setHidden(true);
            form.getWidget(clientId.getName()).setHidden(true);
            form.getWidget(clientSecret.getName()).setHidden(true);
            form.getWidget(clientSecretFile.getName()).setHidden(true);
            form.getWidget(serviceAccountFile.getName()).setHidden(true);
            form.getWidget(useProxy.getName()).setHidden(true);
            form.getWidget(proxyHost.getName()).setHidden(true);
            form.getWidget(proxyPort.getName()).setHidden(true);
            form.getWidget(useSSL.getName()).setHidden(true);
            form.getWidget(sslAlgorithm.getName()).setHidden(true);
            form.getWidget(sslTrustStore.getName()).setHidden(true);
            form.getWidget(sslTrustStorePassword.getName()).setHidden(true);
            //
            if (useOtherConnection) {
                return;
            }

            form.getWidget(applicationName.getName()).setHidden(false);
            form.getWidget(oAuthMethod.getName()).setHidden(false);
            switch (oAuthMethod.getValue()) {
            case AccessToken:
                form.getWidget(accessToken.getName()).setHidden(false);
                break;
            case InstalledApplicationWithIdAndSecret:
                form.getWidget(clientId.getName()).setHidden(false);
                form.getWidget(clientSecret.getName()).setHidden(false);
                break;
            case InstalledApplicationWithJSON:
                form.getWidget(clientSecretFile.getName()).setHidden(false);
                break;
            case ServiceAccount:
                form.getWidget(serviceAccountFile.getName()).setHidden(false);
                break;
            }
            form.getWidget(useProxy.getName()).setHidden(false);
            if (useProxy.getValue()) {
                form.getWidget(proxyHost.getName()).setHidden(false);
                form.getWidget(proxyPort.getName()).setHidden(false);
            }
            form.getWidget(useSSL.getName()).setHidden(false);
            if (useSSL.getValue()) {
                form.getWidget(sslAlgorithm.getName()).setHidden(false);
                form.getWidget(sslTrustStore.getName()).setHidden(false);
                form.getWidget(sslTrustStorePassword.getName()).setHidden(false);
            }
        }
        if (form.getName().equals(Form.ADVANCED)) {
            boolean showDS = InstalledApplicationWithIdAndSecret.equals(oAuthMethod.getValue())
                    || InstalledApplicationWithJSON.equals(oAuthMethod.getValue());
            form.getWidget(datastorePath.getName()).setVisible(showDS);
        }
    }

    public void afterReferencedComponent() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
        refreshLayout(getForm(Form.REFERENCE));
    }

    public void afterOAuthMethod() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.ADVANCED));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(FORM_WIZARD));
    }

    public void afterUseProxy() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(FORM_WIZARD));
    }

    public void afterUseSSL() {
        refreshLayout(getForm(Form.MAIN));
        refreshLayout(getForm(Form.REFERENCE));
        refreshLayout(getForm(FORM_WIZARD));
    }

    public ValidationResult validateTestConnection() throws Exception {
        try {
            SandboxedInstance sandboxedInstance = getSandboxedInstance(SOURCE_OR_SINK_CLASS);
            GoogleDriveRuntime sos = (GoogleDriveRuntime) sandboxedInstance.getInstance();
            ValidationResultMutable vr = new ValidationResultMutable(sos.validateConnection(this));
            if (vr.getStatus() == ValidationResult.Result.OK) {
                getForm(FORM_WIZARD).setAllowForward(true);
                getForm(FORM_WIZARD).setAllowFinish(true);
            } else {
                getForm(FORM_WIZARD).setAllowForward(false);
            }
            return vr;
        } catch (Exception e) {
            return new ValidationResultMutable(Result.ERROR, e.getMessage());
        }
    }

    @Override
    public GoogleDriveConnectionProperties getConnectionProperties() {
        return getEffectiveConnectionProperties();
    }

    /**
     * Return connection properties object which is currently in effect.
     *
     * <p>
     * If this object references to another connection component then a referenced connection properties will be
     * returned. Otherwise, this connection properties object will be returned.
     *
     * @return connection properties object
     */
    public GoogleDriveConnectionProperties getEffectiveConnectionProperties() {
        String refComponentId = getReferencedComponentId();
        if (refComponentId != null) {
            GoogleDriveConnectionProperties properties = getReferencedConnectionProperties();
            if (properties == null) {
                LOG.error("Connection has a reference to '{}' but the referenced object is null!", refComponentId);
            }
            return properties;
        }
        return this;
    }

    /**
     * Return identifier of referenced connection component.
     *
     * @return referenced connection component's ID or {@code null}
     */
    public String getReferencedComponentId() {
        return referencedComponent.componentInstanceId.getStringValue();
    }

    /**
     * Return referenced connection properties.
     *
     * @return referenced connection properties or {@code null}
     */
    public GoogleDriveConnectionProperties getReferencedConnectionProperties() {
        GoogleDriveConnectionProperties refProps = referencedComponent.getReference();
        if (refProps != null) {
            return refProps;
        }
        return null;
    }

}
