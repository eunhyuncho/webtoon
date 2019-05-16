package com.project.picktoon.dto.daumWebtoonDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DaumWebtoonInfo {
    private String nickname;        // url에 들어갈 웹툰이름
    private String title;           // 웹툰 이름 (제목)
    @JsonProperty("introduction")
    private String description;    // 웹툰 설명

    private String pcThumbnailImageUrl;

    private String author1;
    private String author2;

    private String count;
    private String updatedDate;

    private List<String> genres = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();
    private List<String> weekDays = new ArrayList<>();


    // json 중첩된 속성 가져오기..
    @SuppressWarnings("unchecked")
    @JsonProperty("pcThumbnailImage")
    private void unpackNested(Map<String,Object> pcThumbnailImage) {
        this.pcThumbnailImageUrl = (String)pcThumbnailImage.get("url");
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("latestWebtoonEpisode")
    private void findUpdateInfo(Map<String, Object> latestWebtoonEpisode){
        this.count = (String) latestWebtoonEpisode.get("title");
        this.updatedDate = (String) latestWebtoonEpisode.get("dateCreated");
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("cartoon")
    private void findWebtoon(Map<String, Object> webtoon){
        // 작가 이름
        List<Map<String, Object>> artists = (List<Map<String, Object>>) webtoon.get("artists");
        this.author1 = (String) artists.get(0).get("name");
        this.author2 = (String) artists.get(1).get("name");
        // 장르
        List<Map<String, String>> genreList = (List<Map<String, String>>) webtoon.get("genres");
        if(!genreList.isEmpty()){
            for(Map<String, String> genre : genreList)
                this.genres.add(genre.get("name"));
        }

        // 키워드
        List<Map<String, String>> categoryList = (List<Map<String, String>>) webtoon.get("categories");
        if(!categoryList.isEmpty()){
            for(Map<String, String> category : categoryList)
                if(category.get("categoryType").equals("keyword"))
                    this.keywords.add(category.get("name"));
        }

    }

    @SuppressWarnings("unchecked")
    @JsonProperty("webtoonWeeks")
    private void findWebtoonWeekdays(List<Map<String, String>> webtoonWeeks){
        //연재 요일 추가
        for(Map<String, String> weekDay : webtoonWeeks){
            weekDays.add(weekDay.get("weekDay"));
        }


    }
}
