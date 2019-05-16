package com.project.picktoon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SearchWebtoonDto {
    private Long id;
    private String title;
    private String seeAge;
    private List<WebtoonImageDto> webtoonImages;
    private List<KeywordDto> keywords;
    private String platformName;

    private Boolean updateState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date updatedDate;
    private String totalCount;
}
