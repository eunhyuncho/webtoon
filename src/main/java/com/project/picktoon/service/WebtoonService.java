package com.project.picktoon.service;

import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.SearchKeyword;

import java.util.List;

public interface WebtoonService {
    public List<Webtoon> getWebtoons(List<SearchKeyword> keywords, String searchStr, int page);
    public Webtoon getWebtoonById(Long id);
    public Webtoon getWebtoonByTitle(String title);
    public List<Webtoon> getBestWebtoons();
    public Webtoon addWebtoon(Webtoon webtoon);
    public void updateWebtoon(Webtoon webtoon);
    public void deleteWebtoon(Long id);
    public boolean existWebtoonById(Long id);
    public List<Webtoon> getUpdateCheckWebtoon(Long keywordId);
    public Webtoon getWebtoonByTitleAndPlatform(String title, String platform);
    public long getCountByPlatformAndKeyword(String platform, Long keywordId);
    public void updateWebtoonUpdateState(Long keywordId);

}
