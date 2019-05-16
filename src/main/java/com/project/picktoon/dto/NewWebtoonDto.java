package com.project.picktoon.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class NewWebtoonDto {
    @NotNull
    private int id;
    private int ordering;
    @NotNull
    private Long webtoonId;
    @NotNull
    @Size(min=1, max=45)
    private String webtoonTitle;
    @NotNull
    private Long webtoonImageId;
}
