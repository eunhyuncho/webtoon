package com.project.picktoon.dto.daumWebtoonDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaumWebtoonData {
    @JsonProperty("webtoon")
    private DaumWebtoonInfo daumWebtoonInfo;
}
