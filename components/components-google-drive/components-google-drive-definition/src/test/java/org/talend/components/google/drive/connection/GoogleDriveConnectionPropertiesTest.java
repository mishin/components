package org.talend.components.google.drive.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.talend.components.google.drive.GoogleDriveTestBase;
import org.talend.components.google.drive.connection.GoogleDriveConnectionProperties.OAuthMethod;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;

public class GoogleDriveConnectionPropertiesTest extends GoogleDriveTestBase {

    GoogleDriveConnectionProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new GoogleDriveConnectionProperties("test");
        properties.setupProperties();
        properties.setupLayout();
        properties.refreshLayout(properties.getForm(Form.MAIN));
        properties.refreshLayout(properties.getForm(Form.ADVANCED));
    }

    @Test
    public void testOAuthMethod() throws Exception {
        assertEquals("AccessToken", OAuthMethod.AccessToken.name());
        assertEquals(OAuthMethod.AccessToken, OAuthMethod.valueOf("AccessToken"));
        assertEquals("InstalledApplicationWithIdAndSecret", OAuthMethod.InstalledApplicationWithIdAndSecret.name());
        assertEquals(OAuthMethod.InstalledApplicationWithIdAndSecret, OAuthMethod.valueOf("InstalledApplicationWithIdAndSecret"));
        assertEquals("InstalledApplicationWithJSON", OAuthMethod.InstalledApplicationWithJSON.name());
        assertEquals(OAuthMethod.InstalledApplicationWithJSON, OAuthMethod.valueOf("InstalledApplicationWithJSON"));
        assertEquals("ServiceAccount", OAuthMethod.ServiceAccount.name());
        assertEquals(OAuthMethod.ServiceAccount, OAuthMethod.valueOf("ServiceAccount"));
    }

    @Test
    public void testAfterReferencedComponent() throws Exception {
        properties.referencedComponent.componentInstanceId.setValue(GoogleDriveConnectionDefinition.COMPONENT_NAME);
        properties.afterReferencedComponent();
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.oAuthMethod.getName()).isHidden());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.applicationName.getName()).isHidden());
    }

    @Test
    public void testAfterOAuthMethod() throws Exception {
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.accessToken.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientId.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecret.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecretFile.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.serviceAccountFile.getName()).isVisible());
        assertFalse(properties.getForm(Form.ADVANCED).getWidget(properties.datastorePath.getName()).isVisible());
        properties.oAuthMethod.setValue(OAuthMethod.InstalledApplicationWithIdAndSecret);
        properties.afterOAuthMethod();
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.accessToken.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.clientId.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.clientSecret.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecretFile.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.serviceAccountFile.getName()).isVisible());
        assertTrue(properties.getForm(Form.ADVANCED).getWidget(properties.datastorePath.getName()).isVisible());
        properties.oAuthMethod.setValue(OAuthMethod.InstalledApplicationWithJSON);
        properties.afterOAuthMethod();
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.accessToken.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientId.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecret.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.clientSecretFile.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.serviceAccountFile.getName()).isVisible());
        assertTrue(properties.getForm(Form.ADVANCED).getWidget(properties.datastorePath.getName()).isVisible());
        properties.oAuthMethod.setValue(OAuthMethod.ServiceAccount);
        properties.afterOAuthMethod();
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.accessToken.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientId.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecret.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.clientSecretFile.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.serviceAccountFile.getName()).isVisible());
        assertFalse(properties.getForm(Form.ADVANCED).getWidget(properties.datastorePath.getName()).isVisible());
    }

    @Test
    public void testAfterUseProxy() throws Exception {
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.proxyHost.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.proxyPort.getName()).isVisible());
        properties.useProxy.setValue(true);
        properties.afterUseProxy();
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.proxyHost.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.proxyPort.getName()).isVisible());
    }

    @Test
    public void testAfterUseSSL() throws Exception {
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.sslAlgorithm.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.sslTrustStore.getName()).isVisible());
        assertFalse(properties.getForm(Form.MAIN).getWidget(properties.sslTrustStorePassword.getName()).isVisible());
        properties.useSSL.setValue(true);
        properties.afterUseSSL();
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.sslAlgorithm.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.sslTrustStore.getName()).isVisible());
        assertTrue(properties.getForm(Form.MAIN).getWidget(properties.sslTrustStorePassword.getName()).isVisible());
    }

    @Test
    public void testValidateTestConnection() throws Exception {
        assertEquals(Result.ERROR, properties.validateTestConnection().getStatus());
        try (SandboxedInstanceTestFixture sandboxedInstanceTestFixture = new SandboxedInstanceTestFixture()) {
            sandboxedInstanceTestFixture.setUp();
            properties.validateTestConnection();
            assertTrue(properties.getForm(GoogleDriveConnectionProperties.FORM_WIZARD).isAllowForward());
            assertTrue(properties.getForm(GoogleDriveConnectionProperties.FORM_WIZARD).isAllowFinish());
            sandboxedInstanceTestFixture.changeValidateConnectionResult(Result.ERROR);
            properties.validateTestConnection();
            assertFalse(properties.getForm(GoogleDriveConnectionProperties.FORM_WIZARD).isAllowForward());
        }
    }

    @Test
    public void testGetConnectionProperties() throws Exception {
        assertEquals(properties, properties.getConnectionProperties());
    }

    @Test
    public void testGetEffectiveConnectionProperties() throws Exception {
        assertEquals(properties, properties.getEffectiveConnectionProperties());
        properties.referencedComponent.setReference(new GoogleDriveConnectionProperties("referenced"));
        properties.referencedComponent.componentInstanceId.setValue("refcompid");
        assertNotNull(properties.getEffectiveConnectionProperties());
        properties.referencedComponent.setReference(null);
        assertNull(properties.getEffectiveConnectionProperties());
    }

    @Test
    public void testGetReferencedComponentId() throws Exception {
        assertNull(properties.getReferencedComponentId());
        properties.referencedComponent.setReference(new GoogleDriveConnectionProperties("referenced"));
        properties.referencedComponent.componentInstanceId.setValue("refcompid");
        assertNotNull(properties.getReferencedComponentId());
        assertEquals("refcompid", properties.getReferencedComponentId());
    }

    @Test
    public void testGetReferencedConnectionProperties() throws Exception {
        assertNull(properties.getReferencedConnectionProperties());
        properties.referencedComponent.setReference(new GoogleDriveConnectionProperties("referenced"));
        properties.referencedComponent.componentInstanceId.setValue("refcompid");
        assertNotNull(properties.getReferencedConnectionProperties());
    }
}