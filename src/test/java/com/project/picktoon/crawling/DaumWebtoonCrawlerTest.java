package com.project.picktoon.crawling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.picktoon.dto.daumWebtoonDto.DaumWebtoonInfo;
import com.project.picktoon.dto.daumWebtoonDto.DaumWebtoonList;
import com.project.picktoon.dto.daumWebtoonDto.DaumWebtoonSingle;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class DaumWebtoonCrawlerTest {
    RestTemplate restTemplate;

    @Before
    public void init(){
        restTemplate = new RestTemplate();
    }


    @Test
    public void restTemplateTest(){
        String url = "http://webtoon.daum.net/data/pc/webtoon/view/driftprison";

        String result = restTemplate.getForObject(url, String.class);

        log.info(result);
    }

    @Test
    public void 월요일_연재_웹툰_리스트_가져오기(){
        String url = "http://webtoon.daum.net/data/pc/webtoon/list_serialized/mon?timeStamp=1556525714828";
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

    @Test
    public void 웹툰_정보_가져오기() throws Exception{
        String url = "http://webtoon.daum.net/data/pc/webtoon/view/counter";
        DaumWebtoonSingle daumWebtoonSingle = restTemplate.getForObject(url, DaumWebtoonSingle.class);
//        String jsonData = restTemplate.getForObject(url, String.class);
        //Assert.assertEquals(webtoonInfo.getWebtoon().getTitle(), "카운터");
    }

    @Test
    public void 웹툰_업데이트_정보_가져오기() throws Exception{
        String url = "http://webtoon.daum.net/data/pc/webtoon/view/counter";
        String jsonData = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonData);

        // get Node
        JsonNode latestWebtoon = root.path("data").path("webtoon").path("latestWebtoonEpisode");

        if(!latestWebtoon.isMissingNode()){
            log.info("title : " + latestWebtoon.path("title").asText());
            log.info("updatedDate : "+ latestWebtoon.path("dateCreated").asText());
        }

    }
}