package com.project.picktoon.crawling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.picktoon.domain.Platform;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.service.WebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Log
@RequiredArgsConstructor
public class WebtoonTaskScheduler {
    private final WebtoonService webtoonService;
    private final WebDriver driver;
    private final WebDriver driver2;
    private Map<Long, Webtoon> targetWebtoonsNaver = new HashMap<>();
    private Map<Long, Webtoon> targetWebtoonsDaum = new HashMap<>();
    private Map<Long, Webtoon> targetWebtoonsLezhin = new HashMap<>();
    private Map<Long, Webtoon> remainWebtoons = new HashMap<>();
    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Scheduled(cron= "0 5 23 * * *")
    public void renewTargetWebtoons(){
        Calendar calendar = Calendar.getInstance();
        long nowDate = (long)calendar.get(Calendar.DAY_OF_WEEK); // 1(월) ~ 7(일)

        remainWebtoons.putAll(targetWebtoonsNaver);
        targetWebtoonsNaver = new HashMap<>();
        //오늘 연재일인 웹툰의 업데이트 상태를 false로 변경한다.
        webtoonService.updateWebtoonUpdateState(nowDate);

        //다음날 연재일인 웹툰을 가져온다...
        log.info("now date : "+nowDate);
        List<Webtoon> webtoons = webtoonService.getUpdateCheckWebtoon(nowDate + 1); // +1
        for(Webtoon w : webtoons){
            if(!w.getState().equals("연재중")) continue;
            switch (w.getPlatform().getPlatformName()){
                case Naver:
                    targetWebtoonsNaver.put(w.getId(), w);
                    break;
                case Daum:
                    targetWebtoonsDaum.put(w.getId(), w);
                    break;
                case Lezhin:
                    targetWebtoonsLezhin.put(w.getId(), w);
                    break;
            }
        }
        log.info("targetWebtoons(네이버) : " + targetWebtoonsNaver.size());
        log.info("targetWebtoons(다음) : " + targetWebtoonsDaum.size());
        log.info("targetWebtoons(레진) " + targetWebtoonsLezhin.size());

    }

    // 네이버 업데이트 : 10분 마다 체크 !!
    @Scheduled(cron= "0 0/10 * * * *" )
    public void checkUpdateNaver() {
       Iterator<Long> it =  targetWebtoonsNaver.keySet().iterator();
       log.info("네이버 웹툰 업데이트 시작.");
       while(it.hasNext()){
           try {
               Long id = it.next();
               Webtoon webtoon = targetWebtoonsNaver.get(id);
               if(updateNaver(webtoon)){
                   it.remove();
               }
           }catch (Exception ex){
               ex.printStackTrace();
           }
       }
    }

    // 다음 업데이트 : 10분 마다 체크!
    @Scheduled(cron= "0 0/10 * * * *")
    public void checkUpdateDaum(){
        Iterator<Long> it =  targetWebtoonsDaum.keySet().iterator();
        log.info("다음 웹툰 업데이트 시작.");
        while(it.hasNext()){
            try{
                Long id = it.next();
                Webtoon webtoon = targetWebtoonsDaum.get(id);
                if(updateDaum(webtoon)){
                    it.remove();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    // 레진 업데이트 : 10분 마다 체크!
    @Scheduled(cron= "0 0/10 * * * *")
    public void checkLezhinWebtoon(){
        Iterator<Long> it = targetWebtoonsLezhin.keySet().iterator();
        log.info("레진 웹툰 업데이트 시작.");
        while(it.hasNext()){
            Long id = it.next();
            Webtoon webtoon = targetWebtoonsLezhin.get(id);
            if(updateLezhin(webtoon, driver)){
                it.remove();
            }
        }

    }

    @Scheduled(cron= "0 0/30 * * * *")
    public void checkRemainWebtoons(){
        Iterator<Long> it = remainWebtoons.keySet().iterator();
        log.info("남은 웹툰 업데이트 검사 시작");
        while(it.hasNext()){
            Long id = it.next();
            Webtoon webtoon = remainWebtoons.get(id);
            Platform platform = webtoon.getPlatform();
            switch (platform.getPlatformName()){
                case Naver:
                    if(updateNaver(webtoon)) it.remove();
                    break;
                case Daum:
                    if(updateDaum(webtoon)) it.remove();
                    break;
                case Lezhin:
                    if(updateLezhin(webtoon, driver2)) it.remove();
                    break;
            }
        }

    }

    public boolean updateNaver(Webtoon webtoon){
        try {
            log.info("checkwebtoon title (N) : " + webtoon.getTitle());

            Document doc = Jsoup.connect(webtoon.getLink()).timeout(5000).get();

            String newUpdateDateStr = doc.select("td.num").first().text();    // 업데이트 날짜
            String newCount = doc.select("td.title > a").first().text();   // 최신 화
            if (!newCount.equals(webtoon.getTotalCount())) {
                // 총 화수 변경
                webtoon.setTotalCount(newCount);
                // 웹툰 업데이트 날짜 변경
                webtoon.setUpdatedDate(new Date());
                // 웹툰 업데이트 상태 변경
                webtoon.setUpdateState(true);
                // 저장한 후 삭제..
                webtoonService.updateWebtoon(webtoon);
                log.info("웹툰 업데이트 완료 (Naver) : " + webtoon.getTitle());
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateDaum(Webtoon webtoon){
        try {
            log.info("checkwebtoon title (D) : " + webtoon.getTitle());

            String url = webtoon.getCrawlingLink();
            // json 데이터 가져오기..
            String jsonData = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(jsonData);
            // 최신 업데이트 정보 가져오기..
            JsonNode latestWebtoon = root.path("data").path("webtoon").path("latestWebtoonEpisode");
            if (!latestWebtoon.isMissingNode()) {
                String newCount = latestWebtoon.path("title").asText();
                if (!newCount.equals(webtoon.getTotalCount())) {
                    // 총 화수 변경
                    webtoon.setTotalCount(newCount);
                    // 업데이트 날짜 변경
                    webtoon.setUpdatedDate(new Date());
                    // 업데이트 상태 변경
                    webtoon.setUpdateState(true);
                    // 저장
                    webtoonService.updateWebtoon(webtoon);
                    log.info("웹툰 업데이트 완료 (Daum) : " + webtoon.getTitle());
                    return true;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateLezhin(Webtoon webtoon, WebDriver driver){
        try {
            log.info("checkwebtoon title (L) : " + webtoon.getTitle());

            driver.get(webtoon.getCrawlingLink());
            WebElement element = driver.findElement(By.id("comic-episode-list"));
            List<WebElement> li = element.findElements(By.tagName("li"));
            SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
            Date day = null;
            Date now = new Date();
            WebElement webElement = li.get(0);

            for (int i = 1; i < li.size(); i++) {
                WebElement w = li.get(i);
                try {
                    day = format.parse(w.findElement(By.className("free-date")).getText());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (day.after(now)) {
                    webElement = li.get(i - 1);
                    break;
                }

            }

            String count = webElement.findElement(By.className("episode-name")).getText();
            if (!count.equals(webtoon.getTotalCount())) {
                webtoon.setUpdateState(true);
                webtoon.setUpdatedDate(new Date());
                webtoon.setTotalCount(webElement.findElement(By.className("episode-name")).getText());
                webtoonService.updateWebtoon(webtoon);
                log.info("웹툰 업데이트 완료 (Lezhin) : " + webtoon.getTitle());
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;

    }
}
