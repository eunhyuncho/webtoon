package com.project.picktoon.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddNewWebtoonDto {
    @NotNull
    private String title;
    @NotNull
    private Long webtoonId;
    @NotNull
    private Long webtoonImageId = -1L;
}
