package com.project.picktoon.crawling;

import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonInfo;
import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonList;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class DaumWebtoonCrawlerTest {
    @Autowired
    DaumWebtoonCrawler daumWebtoonCrawler;
    RestTemplate restTemplate;

    @Before
    public void init(){
        restTemplate = new RestTemplate();
    }

    @Test
    public void getWebtoonList() {
        log.info("다음 웹툰 크롤링 테스트 시작 !");
        daumWebtoonCrawler.getWebtoonList();
    }

    @Test
    public void restTemplateTest(){
        String url = "http://webtoon.daum.net/data/pc/webtoon/view/driftprison";

        String result = restTemplate.getForObject(url, String.class);

        log.info(result);
    }

    @Test
    public void 금요일_연재_웹툰_리스트_가져오기(){
        String url = "http://webtoon.daum.net/data/pc/webtoon/list_serialized/fri?timeStamp=1556264030375";

        DaumWebtoonList result = restTemplate.getForObject(url , DaumWebtoonList.class);
        log.info("size : "+result.getData().size());
        List<DaumWebtoonInfo> webtoonInfos = result.getData();

        for(DaumWebtoonInfo d : webtoonInfos){
            System.out.println("title : " + d.getTitle());
            System.out.println("nickname : " + d.getNickname());
            System.out.println("introduction : " + d.getDescription());
            System.out.println("pcThumbnailImageUrl : "+ d.getPcThumbnailImageUrl());
            System.out.println("author1 : " + d.getAuthor1());
            System.out.println("author2 : " + d.getAuthor2());
            System.out.println("updatedDate : "+d.getUpdatedDate());
            System.out.println("count : " + d.getCount());

        }
    }
}