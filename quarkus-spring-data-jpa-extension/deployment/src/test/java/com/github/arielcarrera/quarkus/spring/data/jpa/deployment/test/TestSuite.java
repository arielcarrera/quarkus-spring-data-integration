package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuiteDisplayName("Quarkus Spring Data Jpa Extension - Integration")
@SelectPackages("com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test")
public class TestSuite {
}