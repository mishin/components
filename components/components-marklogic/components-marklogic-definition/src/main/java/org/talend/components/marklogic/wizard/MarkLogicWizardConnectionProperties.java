package org.talend.components.marklogic.wizard;

import org.talend.components.marklogic.tmarklogicconnection.MarkLogicConnectionProperties;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.StringProperty;
import org.talend.daikon.properties.service.Repository;

import static org.talend.daikon.properties.presentation.Widget.widget;

public class MarkLogicWizardConnectionProperties extends MarkLogicConnectionProperties {

    public StringProperty nameStoredConnection = PropertyFactory.newString("nameStoredConnection");
    private String repositoryLocation;

    public MarkLogicWizardConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form wizardForm = Form.create(this, Form.MAIN);
        wizardForm.addRow(nameStoredConnection);
        wizardForm.addRow(host);
        wizardForm.addRow(port);
        wizardForm.addRow(database);
        wizardForm.addRow(username);
        wizardForm.addColumn(password);
        wizardForm.addColumn(widget(authentication).setWidgetType(Widget.ENUMERATION_WIDGET_TYPE));
        refreshLayout(wizardForm);

        getForm(Form.MAIN).setAllowBack(true);
        getForm(Form.MAIN).setAllowFinish(true);
    }

    public void beforeFormPresentMain() throws Exception {
        setupLayout();
    }

    public ValidationResult afterFormFinishMain(Repository<Properties> repo) throws Exception {
            String connRepLocation = repo.storeProperties(this, nameStoredConnection.getStringValue(), repositoryLocation, null);

            return ValidationResult.OK;
    }


    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    public MarkLogicWizardConnectionProperties setRepositoryLocation(String location) {
        repositoryLocation = location;
        return this;
    }
}
