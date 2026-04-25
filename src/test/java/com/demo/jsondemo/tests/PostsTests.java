package com.demo.jsondemo.tests;

import com.demo.jsondemo.base.BaseTest;
import com.demo.jsondemo.model.Post;
import com.demo.jsondemo.utils.ApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Posts API")
public class PostsTests extends BaseTest {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String POST_BY_ID_ENDPOINT = "/posts/{id}";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test(description = "获取所有帖子列表")
    @Description("验证获取所有帖子接口返回200和正确的数据结构")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllPosts() {
        logStep("获取所有帖子");

        Response response = ApiUtils.get(POSTS_ENDPOINT);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$").size()).isGreaterThan(0);

        logInfo("成功获取 " + response.jsonPath().getList("$").size() + " 个帖子");
    }

    @Test(description = "根据ID获取单个帖子")
    @Description("验证根据ID获取帖子接口返回正确的数据")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetPostById() {
        int postId = 1;
        logStep("获取ID为 " + postId + " 的帖子");

        Response response = ApiUtils.get(POST_BY_ID_ENDPOINT, postId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        Post post = response.as(Post.class);
        assertThat(post.getId()).isEqualTo(postId);
        assertThat(post.getUserId()).isNotNull();
        assertThat(post.getTitle()).isNotEmpty();

        logInfo("成功获取帖子: " + post.getTitle());
    }

    @Test(description = "创建新帖子")
    @Description("验证创建帖子接口返回201和正确的数据")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreatePost() throws JsonProcessingException {
        Post newPost = Post.createTestPost(1, "Test Title", "Test Body");
        logStep("创建新帖子: " + newPost.getTitle());

        Response response = ApiUtils.post(POSTS_ENDPOINT, objectMapper.writeValueAsString(newPost));

        assertThat(response.getStatusCode()).isEqualTo(201);

        Post createdPost = response.as(Post.class);
        assertThat(createdPost.getTitle()).isEqualTo(newPost.getTitle());
        assertThat(createdPost.getBody()).isEqualTo(newPost.getBody());
        assertThat(createdPost.getId()).isNotNull();

        logInfo("成功创建帖子, ID: " + createdPost.getId());
    }

    @Test(description = "更新帖子")
    @Description("验证更新帖子接口返回200和更新后的数据")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdatePost() throws JsonProcessingException {
        int postId = 1;
        Post updatedPost = Post.createTestPost(1, "Updated Title", "Updated Body");
        logStep("更新ID为 " + postId + " 的帖子");

        Response response = ApiUtils.put(POST_BY_ID_ENDPOINT, postId, objectMapper.writeValueAsString(updatedPost));

        assertThat(response.getStatusCode()).isEqualTo(200);

        Post post = response.as(Post.class);
        assertThat(post.getTitle()).isEqualTo("Updated Title");
        assertThat(post.getBody()).isEqualTo("Updated Body");

        logInfo("成功更新帖子");
    }

    @Test(description = "删除帖子")
    @Description("验证删除帖子接口返回200")
    @Severity(SeverityLevel.NORMAL)
    public void testDeletePost() {
        int postId = 1;
        logStep("删除ID为 " + postId + " 的帖子");

        Response response = ApiUtils.delete(POST_BY_ID_ENDPOINT, postId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        logInfo("成功删除帖子");
    }

    @Test(description = "获取不存在的帖子")
    @Description("验证获取不存在的帖子返回404或空对象")
    @Severity(SeverityLevel.MINOR)
    public void testGetNonExistentPost() {
        int postId = 99999;
        logStep("获取不存在的帖子 ID: " + postId);

        Response response = ApiUtils.get(POST_BY_ID_ENDPOINT, postId);

        // JSONPlaceholder返回404
        assertThat(response.getStatusCode()).isEqualTo(404);

        logInfo("正确返回404");
    }
}
