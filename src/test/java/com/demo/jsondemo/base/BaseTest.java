package com.demo.jsondemo.base;

import com.demo.jsondemo.utils.DatabaseUtils;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class BaseTest {
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    private ThreadLocal<Long> testStartTime = new ThreadLocal<>();

    @BeforeSuite
    public void setUpSuite() {
        logger.info("========== Test Suite Started ==========");
        DatabaseUtils.connect();
    }

    @AfterSuite
    public void tearDownSuite() {
        DatabaseUtils.disconnect();
        logger.info("========== Test Suite Finished ==========");
    }

    @BeforeMethod
    public void beforeMethod() {
        testStartTime.set(System.currentTimeMillis());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime.get();
        String status = result.isSuccess() ? "PASSED" : "FAILED";
        String errorMessage = result.isSuccess() ? null :
                (result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");

        DatabaseUtils.insertTestResult(
                result.getName(),
                result.getTestClass().getName(),
                status,
                duration,
                errorMessage
        );
    }

    protected void logStep(String stepName) {
        logger.info("Step: {}", stepName);
        Allure.step(stepName);
    }

    protected void logInfo(String message) {
        logger.info(message);
        Allure.attachment("Info", message);
    }
}
