package com.prj.cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingEntry {
    private String nickname;
    private Integer score;
    private Integer rank;
    private LocalDateTime playedAt;
}
