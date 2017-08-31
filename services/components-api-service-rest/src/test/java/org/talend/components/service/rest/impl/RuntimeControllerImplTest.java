// ==============================================================================
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
// ==============================================================================

package org.talend.components.service.rest.impl;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.talend.components.service.rest.AbstractSpringIntegrationTests;
import org.talend.components.service.rest.ServiceConstants;
import org.talend.components.service.rest.dto.SerPropertiesDto;
import org.talend.components.service.rest.dto.UiSpecsPropertiesDto;
import org.talend.components.service.rest.mock.MockDatasetRuntime;

import com.jayway.restassured.response.Response;

public class RuntimeControllerImplTest extends AbstractSpringIntegrationTests {

    protected String getVersionPrefix() {
        return ServiceConstants.V0;
    }

    @Test
    public void validateConnectionUiSpecs() throws Exception {
        UiSpecsPropertiesDto propertiesDto = buildTestDataStoreFormData();
        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(HttpStatus.OK.value()).log().ifError() //
                .with().content(propertiesDto) //
                .contentType(ServiceConstants.MLTPL_UI_SPEC_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/check");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);
    }

    @Test
    public void validateConnectionJsonio() throws Exception {
        SerPropertiesDto propertiesDto = buildTestDataStoreSerProps();
        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(HttpStatus.OK.value()).log().ifError() //
                .with().content(propertiesDto) //
                .contentType(ServiceConstants.MULTPL_JSONIO_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/check");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);
    }

    @Test
    public void getDatasetSchemaUiSpec() throws Exception {
        // given
        UiSpecsPropertiesDto formDataContainer = buildTestDataSetFormData();

        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(200).log().ifError() //
                .with().content(formDataContainer) //
                .contentType(ServiceConstants.MLTPL_UI_SPEC_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/schema");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);
        assertEquals(MockDatasetRuntime.getSchemaJsonRepresentation(), content);
    }

    @Test
    public void getDatasetSchemaJsonIo() throws Exception {
        // given
        SerPropertiesDto formDataContainer = buildTestDataSetSerProps();

        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(200).log().ifError() //
                .with().content(formDataContainer) //
                .contentType(ServiceConstants.MULTPL_JSONIO_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/schema");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);
        assertEquals(MockDatasetRuntime.getSchemaJsonRepresentation(), content);
    }

    @Test
    public void getDatasetDataUisSpecs() throws Exception {
        // given
        UiSpecsPropertiesDto formDataContainer = buildTestDataSetFormData();

        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(200).log().ifError() //
                .with().content(formDataContainer) //
                .contentType(ServiceConstants.MLTPL_UI_SPEC_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/data");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);

        assertEquals(MockDatasetRuntime.getRecordJsonRepresentation(), content);
    }

    @Test
    public void getDatasetDataJsonio() throws Exception {
        // given
        SerPropertiesDto formDataContainer = buildTestDataSetSerProps();

        // when
        Response response = given().accept(APPLICATION_JSON_UTF8_VALUE) //
                .expect() //
                .statusCode(200).log().ifError() //
                .with().content(formDataContainer) //
                .contentType(ServiceConstants.MULTPL_JSONIO_CONTENT_TYPE) //
                .post(getVersionPrefix() + "/runtimes/data");

        // then
        assertNotNull(response);
        String content = response.asString();
        assertNotNull(content);

        assertEquals(MockDatasetRuntime.getRecordJsonRepresentation(), content);
    }
}
