package com.demo.jsondemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;

    public static Post createTestPost(Integer userId, String title, String body) {
        return Post.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .build();
    }
}
