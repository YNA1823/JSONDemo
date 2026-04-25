package com.demo.jsondemo.tests;

import com.demo.jsondemo.base.BaseTest;
import com.demo.jsondemo.model.Post;
import com.demo.jsondemo.utils.ApiUtils;
import com.demo.jsondemo.utils.ExcelReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Posts API - Data Driven")
public class PostsDataDrivenTests extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DataProvider(name = "postData")
    public Object[][] getPostData() {
        String excelPath = "src/main/resources/testdata/posts.xlsx";
        List<Map<String, String>> data = ExcelReader.readData(excelPath);
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }

    @Test(dataProvider = "postData", description = "数据驱动创建帖子")
    @Description("从Excel读取数据创建帖子")
    @Severity(SeverityLevel.NORMAL)
    public void testCreatePostFromExcel(Map<String, String> testData) throws JsonProcessingException {
        String title = testData.get("title");
        String body = testData.get("body");
        int userId = Integer.parseInt(testData.get("userId"));

        logStep("创建帖子: " + title);

        Post newPost = Post.createTestPost(userId, title, body);
        Response response = ApiUtils.post(POSTS_ENDPOINT, objectMapper.writeValueAsString(newPost));

        assertThat(response.getStatusCode()).isEqualTo(201);

        Post createdPost = response.as(Post.class);
        assertThat(createdPost.getTitle()).isEqualTo(title);
        assertThat(createdPost.getBody()).isEqualTo(body);

        logInfo("数据驱动测试通过: " + title);
    }

    @DataProvider(name = "postIds")
    public Object[][] getPostIds() {
        return new Object[][]{
                {1},
                {10},
                {50},
                {100}
        };
    }

    @Test(dataProvider = "postIds", description = "参数化获取帖子")
    @Description("使用不同ID参数化测试获取帖子接口")
    @Severity(SeverityLevel.NORMAL)
    public void testGetPostByIdParameterized(int postId) {
        logStep("获取帖子 ID: " + postId);

        Response response = ApiUtils.get("/posts/{id}", postId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        Post post = response.as(Post.class);
        assertThat(post.getId()).isEqualTo(postId);

        logInfo("成功获取帖子 ID: " + postId);
    }
}
