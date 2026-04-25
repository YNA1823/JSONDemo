package com.demo.jsondemo.utils;

import com.demo.jsondemo.model.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 测试数据生成器工具
 * 用于自动生成各种类型的测试数据
 */
public class TestDataGenerator {

    private static final Random RANDOM = new Random();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String[] TITLE_PREFIXES = {
            "测试", "Test", "API", "自动化", "Automation", "Demo", "示例"
    };

    private static final String[] BODY_TEMPLATES = {
            "这是一个测试内容，用于验证接口功能。",
            "Test content for API validation.",
            "自动化测试数据，包含中文字符。",
            "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?",
            "Number test: 123456789",
            "Mixed content: 测试Test123!@#"
    };

    private static final String[] SPECIAL_CHARS = {
            "", " ", "  ", "\n", "\t", "<script>alert('xss')</script>",
            "'; DROP TABLE posts; --", "../../../etc/passwd"
    };

    private static final int[] USER_ID_BOUNDARIES = {0, 1, 10, 100, Integer.MAX_VALUE, -1, -100};

    /**
     * 生成随机正常 Post 数据
     */
    public static Post generateRandomPost() {
        String title = TITLE_PREFIXES[RANDOM.nextInt(TITLE_PREFIXES.length)] + "_" + generateRandomString(8);
        String body = BODY_TEMPLATES[RANDOM.nextInt(BODY_TEMPLATES.length)] + "_" + generateRandomString(5);
        int userId = RANDOM.nextInt(10) + 1;

        return Post.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .build();
    }

    /**
     * 批量生成随机 Post 数据
     */
    public static List<Post> generateRandomPosts(int count) {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            posts.add(generateRandomPost());
        }
        return posts;
    }

    /**
     * 生成边界值测试数据
     */
    public static List<Post> generateBoundaryPosts() {
        List<Post> posts = new ArrayList<>();

        // 空字符串
        posts.add(Post.builder().userId(1).title("").body("").build());

        // 空格字符串
        posts.add(Post.builder().userId(1).title("   ").body("   ").build());

        // 超长字符串
        posts.add(Post.builder().userId(1).title(generateLongString(1000)).body(generateLongString(5000)).build());

        // 最小有效数据
        posts.add(Post.builder().userId(1).title("a").body("b").build());

        // 边界 userId
        posts.add(Post.builder().userId(0).title("userId为0").body("边界测试").build());
        posts.add(Post.builder().userId(1).title("userId为1").body("边界测试").build());
        posts.add(Post.builder().userId(100).title("userId为100").body("边界测试").build());

        // 特殊字符
        for (String specialChar : SPECIAL_CHARS) {
            posts.add(Post.builder().userId(1).title(specialChar).body(specialChar).build());
        }

        return posts;
    }

    /**
     * 生成异常测试数据（用于负面测试）
     * 返回 Map 格式，包含描述和无效数据
     */
    public static List<Map<String, Object>> generateInvalidPosts() {
        List<Map<String, Object>> invalidData = new ArrayList<>();

        // userId 为 null
        Map<String, Object> case1 = new HashMap<>();
        case1.put("description", "userId为null");
        case1.put("data", createJsonMap(null, "test", "test"));
        invalidData.add(case1);

        // userId 为负数
        Map<String, Object> case2 = new HashMap<>();
        case2.put("description", "userId为负数");
        case2.put("data", createJsonMap(-1, "test", "test"));
        invalidData.add(case2);

        // userId 为字符串（类型错误，用 Map 模拟）
        Map<String, Object> case3 = new HashMap<>();
        case3.put("description", "userId类型错误");
        Map<String, Object> wrongTypeData = new HashMap<>();
        wrongTypeData.put("userId", "not_a_number");
        wrongTypeData.put("title", "test");
        wrongTypeData.put("body", "test");
        case3.put("data", wrongTypeData);
        invalidData.add(case3);

        // title 为 null
        Map<String, Object> case4 = new HashMap<>();
        case4.put("description", "title为null");
        case4.put("data", createJsonMap(1, null, "test"));
        invalidData.add(case4);

        // body 为 null
        Map<String, Object> case5 = new HashMap<>();
        case5.put("description", "body为null");
        case5.put("data", createJsonMap(1, "test", null));
        invalidData.add(case5);

        // 所有字段为 null
        Map<String, Object> case6 = new HashMap<>();
        case6.put("description", "所有字段为null");
        case6.put("data", createJsonMap(null, null, null));
        invalidData.add(case6);

        // 空 JSON 对象
        Map<String, Object> case7 = new HashMap<>();
        case7.put("description", "空JSON对象");
        case7.put("data", new HashMap<>());
        invalidData.add(case7);

        return invalidData;
    }

    /**
     * 生成组合测试数据（用于参数化测试）
     * 返回 Object[][] 格式，兼容 TestNG DataProvider
     */
    public static Object[][] generateDataProviderData(int count) {
        Object[][] data = new Object[count][3];

        for (int i = 0; i < count; i++) {
            Post post = generateRandomPost();
            data[i][0] = post.getUserId();
            data[i][1] = post.getTitle();
            data[i][2] = post.getBody();
        }

        return data;
    }

    /**
     * 生成随机字符串
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的字符串
     */
    public static String generateLongString(int length) {
        StringBuilder sb = new StringBuilder();
        String base = "abcdefghijklmnopqrstuvwxyz";
        while (sb.length() < length) {
            sb.append(base);
        }
        return sb.substring(0, length);
    }

    /**
     * 将 Post 对象转换为 JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    private static Map<String, Object> createJsonMap(Integer userId, String title, String body) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("title", title);
        map.put("body", body);
        return map;
    }
}
