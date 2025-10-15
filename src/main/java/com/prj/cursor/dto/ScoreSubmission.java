package com.prj.cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSubmission {
    private Long userId;
    private String nickname;
    private Integer score;
    private String gameType;
}
