package com.project.picktoon.controller.api;

import com.project.picktoon.domain.Keyword;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.domain.WebtoonImage;
import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonInfo;
import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonList;
import com.project.picktoon.dto.LoadWebtoonData;
import com.project.picktoon.dto.LoadWebtoonLink;
import com.project.picktoon.dto.Result;
import com.project.picktoon.service.KeywordService;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.service.WebtoonService;
import com.project.picktoon.util.KeywordType;
import com.project.picktoon.util.PlatformType;
import com.project.picktoon.util.SeeAge;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Log
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiControllor {
    private final WebtoonService webtoonService;
    private final KeywordService keywordService;
    private final PlatformService platformService;
    private final WebDriver driver;

    @PostMapping("/loadwebtoon/naver")
    public LoadWebtoonData loadWebtoonNaver(@RequestBody LoadWebtoonLink loadWebtoonLink){
        try {
            //웹에서 내용을 가져온다.
            Document doc = Jsoup.connect(loadWebtoonLink.getLink()).timeout(5000).get();

            //내용 중에서 원하는 부분을 가져온다.
            String title = doc.select(".detail h2").first().ownText();  // 제목
            String authorsStr = doc.select(".wrt_nm").text(); // 작가
            String description = doc.select(".detail p").text();    // 설명
            String updateDateStr = doc.select("td.num").first().text();    // 업데이트 날짜
            String count = doc.select("td.title > a").first().text();   // 최신 화
            String imgUrl = doc.select(".thumb img").first().getElementsByAttribute("src").attr("src");

            LoadWebtoonData loadWebtoonData = new LoadWebtoonData();
            loadWebtoonData.setTitle(title);
            loadWebtoonData.setLink(loadWebtoonLink.getLink());

            //작가 -- 네이버는 작가가 2명일 경우 /로 구분한다.
            authorsStr = authorsStr.replace(" ", "");
            String[] authors = authorsStr.split("/");

            for(String a : authors){
                loadWebtoonData.addAuthor(a);
            }

            loadWebtoonData.setDescription(description);
            loadWebtoonData.setUpdatedDate(updateDateStr);
            loadWebtoonData.setCount(count);
            loadWebtoonData.setImgUrl(imgUrl);

            return loadWebtoonData;

        } catch (IOException e) { //Jsoup의 connect 부분에서 IOException 오류가 날 수 있으므로 사용한다.

            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/webtoons/daum")
    public ResponseEntity<Result> loadWebtoonDaum(@RequestBody LoadWebtoonLink loadWebtoonLink){
        // 요일별 웹툰 가져오기.
        RestTemplate restTemplate = new RestTemplate();
        DaumWebtoonList daumWebtoonList = restTemplate.getForObject(loadWebtoonLink.getLink() , DaumWebtoonList.class);
        List<DaumWebtoonInfo> webtoonInfos = daumWebtoonList.getData();

        log.info("웹툰 인포 갯수 : " + webtoonInfos.size());
        log.info("업데이트 요일 : " + loadWebtoonLink.getUpdateDate());
        // 다음 웹툰 - 연재요일로 저장된 웹툰 갯수를 가져온다.
        Long countWebtoons = webtoonService.getCountByPlatformAndKeyword(PlatformType.Daum.toString() , loadWebtoonLink.getUpdateDate());
        log.info("저장된 웹툰 갯수 : "+ countWebtoons);
        Result result = new Result();

        if(countWebtoons >= webtoonInfos.size()){
            result.setResult("이미 모두 등록되어있습니다.");
            return new ResponseEntity<Result>(result, HttpStatus.OK);
        }

        int addWebtoonCount = 0;

        for(DaumWebtoonInfo webtoonInfo : webtoonInfos){
            Webtoon existWebtoon = webtoonService.getWebtoonByTitleAndPlatform(webtoonInfo.getTitle(), PlatformType.Daum.toString());
            if(existWebtoon != null){
                log.info("겹치는 웹툰 : " + existWebtoon.getTitle());
                continue;
            }

            log.info("웹툰 정보 저장 시작");
            Webtoon webtoon = new Webtoon();
            List<Keyword> keywords = new ArrayList<>();
            webtoon.setTitle(webtoonInfo.getTitle());
            webtoon.setSeeAge(SeeAge.SEEAGE_ALL);
            webtoon.setDescription(webtoonInfo.getDescription());
            // 최근 업데이트 날짜
            String updatedDateStr = webtoonInfo.getUpdatedDate();
            updatedDateStr = updatedDateStr.substring(0 , 8);
            log.info("최신 업데이트 날짜 : "+updatedDateStr);
            Date updatedDate = parseDate(updatedDateStr);
            webtoon.setUpdatedDate(updatedDate);

            webtoon.setTotalCount(webtoonInfo.getCount());
            // 플랫폼
            webtoon.setPlatform(platformService.getPlatformByPlatformName(PlatformType.Daum.toString()));

            // 작가 추가.
            Keyword existAuthor = keywordService.getAuthorByName(webtoonInfo.getAuthor1());

            if(existAuthor == null){
                Keyword author1 = Keyword.builder().keywordType(KeywordType.KEYWORD_AUTHOR).keywordValue(webtoonInfo.getAuthor1()).build();
                keywords.add(keywordService.addKeyword(author1));
            }else{
                keywords.add(existAuthor);
            }

            if(!webtoonInfo.getAuthor1().equals(webtoonInfo.getAuthor2())){
                existAuthor = keywordService.getAuthorByName(webtoonInfo.getAuthor2());
                if(existAuthor == null){
                    Keyword author2 = Keyword.builder().keywordType(KeywordType.KEYWORD_AUTHOR).keywordValue(webtoonInfo.getAuthor2()).build();
                    keywords.add(keywordService.addKeyword(author2));
                }else{
                    keywords.add(existAuthor);
                }
            }
            // 링크 추가 ( 링크형식 : http://webtoon.daum.net/webtoon/view/goodbyeinlaw)
            webtoon.setLink("http://webtoon.daum.net/webtoon/view/"+webtoonInfo.getNickname());
            // 크롤링 링크 추가 (링크 형식 : http://webtoon.daum.net/data/pc/webtoon/view/goodbyeinlaw )
            webtoon.setCrawlingLink("http://webtoon.daum.net/data/pc/webtoon/view/"+webtoonInfo.getNickname());

            //장르
            for(String g : webtoonInfo.getGenres()){
                log.info("장르 : "+ g);
                Keyword existGenre = keywordService.getKeywordByTypeAndValue(KeywordType.KEYWORD_GENRE , g);
                if(existGenre == null){
                    Keyword newGenre = Keyword.builder().keywordType(KeywordType.KEYWORD_GENRE).keywordValue(g).build();
                    keywords.add(keywordService.addKeyword(newGenre));
                }else{
                    keywords.add(existGenre);
                }
            }
            //키워드
            for(String k : webtoonInfo.getKeywords()){
                log.info("키워드 :" + k);
                Keyword existKeyword = keywordService.getKeywordByValue(k);
                if(existKeyword == null){
                    Keyword newKeyword = Keyword.builder().keywordType(KeywordType.KEYWORD_KEYWORD).keywordValue(k).build();
                    keywords.add(keywordService.addKeyword(newKeyword));
                }else{
                    keywords.add(existKeyword);
                }
            }
            //연재일
            for(String w : webtoonInfo.getWeekDays()){
                Long keywordId = parseWeekday(w);
                if(keywordId == -1L){
                    continue;
                }else{
                    keywords.add(keywordService.getKeywordById(keywordId));
                }
            }
            webtoon.setKeywords(keywords);
            // 이미지 저장 및 웹툰에 추가.
            webtoon.addWebtoonImage(saveFileFromUrl(webtoonInfo.getPcThumbnailImageUrl(), webtoonInfo.getTitle(),PlatformType.Daum.toString()));
            // 웹툰 저장
            webtoonService.addWebtoon(webtoon);
            addWebtoonCount++;
        }
        result.setResult("저장한 웹툰 갯수 : "+ addWebtoonCount);
        return new ResponseEntity<Result>(result , HttpStatus.OK);
    }

    @PostMapping("loadwebtoon/lezhin")
    public ResponseEntity<LoadWebtoonData> loadWebtoonLezhin(@RequestBody LoadWebtoonLink loadWebtoonLink){

        driver.get(loadWebtoonLink.getLink());
        LoadWebtoonData loadWebtoonData = new LoadWebtoonData();
        String title = driver.findElement(By.className("title")).getText(); //제목
        List<WebElement> authors = driver.findElement(By.className("artist")).findElements(By.tagName("a")); // 작가
        String[] genres = driver.findElement(By.className("genre")).getText().substring(4).split("/");  // 장르
        String description = driver.findElement(By.id("product-synopsis")).getText().substring(3);  // 설명
        String imageUrl = driver.findElement(By.className("thumbnail")).getAttribute("src");    // 이미지 url

        // 제목
        loadWebtoonData.setTitle(title);
        // 작가
        for(WebElement a : authors)
            loadWebtoonData.addAuthor(a.getText());
        // 장르 -- 아이디들을 넘겨준다.
        for(String g : genres){
            Keyword existGenre = keywordService.getKeywordByTypeAndValue(KeywordType.KEYWORD_GENRE , g);
            if(existGenre == null){
                Keyword newGenre = Keyword.builder().keywordType(KeywordType.KEYWORD_GENRE).keywordValue(g).build();
                loadWebtoonData.addGenre(keywordService.addKeyword(newGenre).getId());
            }else{
                loadWebtoonData.addGenre(existGenre.getId());
            }
        }
        // 설명
        loadWebtoonData.setDescription(description);
        // 이미지 주소
        imageUrl = imageUrl.replaceAll("width=100", "width=200");
        loadWebtoonData.setImgUrl(imageUrl);
        log.info("imageUrl : "+imageUrl);

        WebElement element = driver.findElement(By.id("comic-episode-list"));
        List<WebElement> li  = element.findElements(By.tagName("li"));

        log.info("list 사이즈 : "+ li.size());
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Date day = null;
        Date now = new Date();
        WebElement lastUpdatedEpisode = li.get(li.size() - 1);
        // 업데이트 날짜 찾기
        for(int i = 0; i < li.size() ; i++){
            WebElement w = li.get(i);
            String dayStr = w.findElement(By.className("free-date")).getText();
            try{
                day = format.parse(dayStr);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if(i!=0 && day.after(now)){
                lastUpdatedEpisode = li.get(i-1);
                break;
            }

        }
        String updatedDate = lastUpdatedEpisode.findElement(By.className("free-date")).getText();
        updatedDate = "20" + updatedDate;
        log.info("updatedDate = "+updatedDate);
        String count = lastUpdatedEpisode.findElement(By.className("episode-name")).getText();

        loadWebtoonData.setUpdatedDate(updatedDate);
        loadWebtoonData.setCount(count);
        loadWebtoonData.setLink(loadWebtoonLink.getLink());


        return new ResponseEntity(loadWebtoonData, HttpStatus.OK);
    }


    public Date parseDate(String dateStr){
        try{
            Date date = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
            return date;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public Long parseWeekday(String weekdayStr){
        Long id = -1L;
        switch (weekdayStr){
            case "mon":
                id = 2L;
                break;
            case "the":
                id = 3L;
                break;
            case "wed":
                id = 4L;
                break;
            case "thu":
                id = 5L;
                break;
            case "fri":
                id = 6L;
                break;
            case "sat":
                id = 7L;
                break;
            case "sun":
                id = 1L;
                break;
            default:
                break;
        }
        return id;
    }

    // 크롤링한 이미지 저장.
    private WebtoonImage saveFileFromUrl(String url, String title, String platform){
        String dir = "imagefile/webtoon/";
        WebtoonImage webtoonImage = new WebtoonImage();
        Calendar calendar = Calendar.getInstance();
        dir = dir + calendar.get(Calendar.YEAR);
        dir = dir + "/";
        dir = dir + (calendar.get(Calendar.MONTH) + 1);
        dir = dir + "/";
        dir = dir + calendar.get(Calendar.DAY_OF_MONTH);
        dir = dir + "/";
        dir = dir + platform + "/"; // 플랫폼 디렉토리..
        File dirFile = new File(dir);
        dirFile.mkdirs(); // 디렉토리가 없을 경우 만든다. 퍼미션이 없으면 생성안될 수 있다.
        dir = dir + title;
        try{
            URL imgUrl = new URL(url);
            BufferedImage jpg = ImageIO.read(imgUrl);
            File file = new File(dir+".jpg");
            ImageIO.write(jpg, "jpg", file);
            System.out.println("file length : "+file.length());

            webtoonImage.setSaveFileName(dir+".jpg");
            webtoonImage.setLength(file.length());

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return webtoonImage;
    }
}
