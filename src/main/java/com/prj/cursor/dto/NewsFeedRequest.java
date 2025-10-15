package com.prj.cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsFeedRequest {
    private Long userId;
    private String category;
    private int page = 0;
    private int size = 10;
}
