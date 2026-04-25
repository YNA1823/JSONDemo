package com.demo.jsondemo.tests;

import com.demo.jsondemo.base.BaseTest;
import com.demo.jsondemo.model.Post;
import com.demo.jsondemo.utils.ApiUtils;
import com.demo.jsondemo.utils.ExcelWriter;
import com.demo.jsondemo.utils.TestDataGenerator;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试数据生成器演示测试
 * 展示如何使用 TestDataGenerator 工具
 */
@Feature("Test Data Generator")
public class DataGeneratorTests extends BaseTest {

    @Test(description = "使用随机生成的数据创建帖子")
    @Description("演示 TestDataGenerator 生成随机测试数据")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePostWithRandomData() {
        logStep("生成随机测试数据");
        Post post = TestDataGenerator.generateRandomPost();
        logInfo("生成的数据: " + TestDataGenerator.toJson(post));

        logStep("发送POST请求");
        Response response = ApiUtils.post("/posts", TestDataGenerator.toJson(post));

        logStep("验证响应");
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("title")).isEqualTo(post.getTitle());
    }

    @Test(description = "批量生成并测试多个帖子")
    @Description("演示批量生成测试数据")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePostsWithBatchData() {
        logStep("批量生成5条测试数据");
        List<Post> posts = TestDataGenerator.generateRandomPosts(5);
        logInfo("生成了 " + posts.size() + " 条数据");

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            Response response = ApiUtils.post("/posts", TestDataGenerator.toJson(post));

            assertThat(response.getStatusCode()).isEqualTo(201);
            logInfo("第" + (i + 1) + "条数据测试通过");
        }
    }

    @Test(description = "边界值测试")
    @Description("演示边界值测试数据生成")
    @Severity(SeverityLevel.NORMAL)
    public void testBoundaryData() {
        logStep("生成边界值测试数据");
        List<Post> boundaryPosts = TestDataGenerator.generateBoundaryPosts();

        logStep("导出边界值数据到Excel");
        String filePath = "target/boundary_test_data.xlsx";
        ExcelWriter.exportBoundaryDataToExcel(boundaryPosts, filePath);
        logInfo("数据已导出到: " + filePath);

        logInfo("共生成 " + boundaryPosts.size() + " 条边界值数据");
        assertThat(boundaryPosts).isNotEmpty();
    }

    @Test(description = "异常数据测试")
    @Description("演示异常测试数据生成")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidData() {
        logStep("生成异常测试数据");
        List<Map<String, Object>> invalidData = TestDataGenerator.generateInvalidPosts();

        logStep("导出异常数据到Excel");
        String filePath = "target/invalid_test_data.xlsx";
        ExcelWriter.exportInvalidDataToExcel(invalidData, filePath);
        logInfo("数据已导出到: " + filePath);

        for (Map<String, Object> item : invalidData) {
            String description = (String) item.get("description");
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) item.get("data");

            Response response = ApiUtils.post("/posts", TestDataGenerator.toJson(data));
            logInfo(description + " - 状态码: " + response.getStatusCode());
        }

        logInfo("共生成 " + invalidData.size() + " 条异常数据");
    }

    @Test(description = "导出测试数据到Excel")
    @Description("演示导出功能，生成可复用的测试数据文件")
    @Severity(SeverityLevel.MINOR)
    public void testExportDataToExcel() {
        logStep("生成10条随机测试数据");
        List<Post> posts = TestDataGenerator.generateRandomPosts(10);

        logStep("导出到Excel");
        String filePath = "target/generated_test_data.xlsx";
        ExcelWriter.exportPostsToExcel(posts, filePath);

        logInfo("测试数据已导出到: " + filePath);
        assertThat(posts).hasSize(10);
    }
}
