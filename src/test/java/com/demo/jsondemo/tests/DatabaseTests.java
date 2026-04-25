package com.demo.jsondemo.tests;

import com.demo.jsondemo.base.BaseTest;
import com.demo.jsondemo.utils.ApiUtils;
import com.demo.jsondemo.utils.DatabaseUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Database Integration")
public class DatabaseTests extends BaseTest {

    @BeforeClass
    public void setUpTestData() {
        if (DatabaseUtils.isConnected()) {
            logStep("初始化测试数据");
            DatabaseUtils.insertTestData("posts_crud", "/posts", "GET", 200, "获取所有帖子");
            DatabaseUtils.insertTestData("posts_crud", "/posts/{id}", "GET", 200, "根据ID获取帖子");
            DatabaseUtils.insertTestData("posts_crud", "/posts", "POST", 201, "创建新帖子");
            DatabaseUtils.insertTestData("posts_crud", "/posts/{id}", "PUT", 200, "更新帖子");
            DatabaseUtils.insertTestData("posts_crud", "/posts/{id}", "DELETE", 200, "删除帖子");
        }
    }

    @Test(description = "从数据库读取测试数据并执行测试")
    @Description("验证从MySQL读取测试数据执行接口测试")
    @Severity(SeverityLevel.NORMAL)
    public void testWithDatabaseData() {
        if (!DatabaseUtils.isConnected()) {
            logInfo("数据库未启用，跳过测试");
            return;
        }

        logStep("从数据库读取测试数据");
        List<Map<String, Object>> testData = DatabaseUtils.getTestData("posts_crud");

        assertThat(testData).isNotEmpty();

        for (Map<String, Object> data : testData) {
            String endpoint = (String) data.get("endpoint");
            String method = (String) data.get("method");
            int expectedStatus = (Integer) data.get("expected_status");

            logStep(String.format("执行测试: %s %s, 期望状态码: %d", method, endpoint, expectedStatus));

            Response response = executeRequest(endpoint, method);
            if (response != null) {
                assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
            }
        }

        logInfo("数据库驱动测试完成，共执行 " + testData.size() + " 条测试数据");
    }

    @Test(description = "查询测试结果统计")
    @Description("验证测试结果已正确存储到数据库")
    @Severity(SeverityLevel.MINOR)
    public void testQueryTestResults() {
        if (!DatabaseUtils.isConnected()) {
            logInfo("数据库未启用，跳过测试");
            return;
        }

        logStep("查询测试结果统计");

        String sql = "SELECT status, COUNT(*) as count FROM test_results GROUP BY status";
        List<Map<String, Object>> results = DatabaseUtils.executeQuery(sql);

        logInfo("测试结果统计: " + results);
        assertThat(results).isNotEmpty();
    }

    private Response executeRequest(String endpoint, String method) {
        if ("GET".equals(method)) {
            if (endpoint.contains("{id}")) {
                return ApiUtils.get(endpoint, 1);
            }
            return ApiUtils.get(endpoint);
        } else if ("POST".equals(method)) {
            return ApiUtils.post(endpoint, "{\"title\":\"test\",\"body\":\"test\",\"userId\":1}");
        } else if ("PUT".equals(method)) {
            return ApiUtils.put(endpoint, 1, "{\"title\":\"test\",\"body\":\"test\",\"userId\":1}");
        } else if ("DELETE".equals(method)) {
            return ApiUtils.delete(endpoint, 1);
        }
        return null;
    }
}
