package myfirstplaywrightpackage.cucumber;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "myfirstplaywrightpackage.cucumber.stepdefinitions"
)
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
        "pretty," + "html:target/cucumber-reports/cucumber.html"
)
public class CucumberTests {
}
