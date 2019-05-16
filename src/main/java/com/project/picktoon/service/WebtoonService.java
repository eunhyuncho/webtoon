package com.project.picktoon.service;

import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.SearchKeyword;
import com.project.picktoon.util.PlatformType;

import java.util.List;

public interface WebtoonService {
    public List<Webtoon> getWebtoons(List<SearchKeyword> keywords, String searchStr, int page);
    public Webtoon getWebtoonById(Long id);
    public List<Webtoon> getBestWebtoons();
    public Webtoon addWebtoon(Webtoon webtoon);
    public void updateWebtoon(Webtoon webtoon);
    public void deleteWebtoon(Long id);
    public boolean existWebtoonById(Long id);
    public List<Webtoon> getUpdateCheckWebtoon(Long keywordId);
    public Webtoon getWebtoonByTitleAndPlatform(String title, PlatformType platform);
    public long getCountByPlatformAndKeyword(PlatformType platform, Long keywordId);
    public void updateWebtoonUpdateState(Long keywordId);

}
