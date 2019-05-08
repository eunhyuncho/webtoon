package com.project.picktoon.crawling;


import lombok.extern.java.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SeleniumCrawlingTest {
    private WebDriver driver;

    @Before
    public void init(){
        // 크롬 버전  : 74
        //System.setProperty("webdriver.chrome.driver", "src/test/resources/driver/chromedriver");
        String classpathRelativeLocation = "driver/chromedriver";
        URL url = this.getClass().getClassLoader().getResource(classpathRelativeLocation);
        File file = new File(url.getFile());
        ChromeDriverService.Builder bldr = (new ChromeDriverService.Builder())
                .usingDriverExecutable(file)
                .usingAnyFreePort();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

        driver = new ChromeDriver(bldr.build(), options);
    }

    @After
    public void after() throws Exception{
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void 크롤링테스트() {
        driver.get("https://www.lezhin.com/ko/comic/sparrow");
        //List<WebElement> episodeSeq = driver.findElements(By.className("episode-seq"));
        String title = driver.findElement(By.className("title")).getText();
        List<WebElement> authors = driver.findElement(By.className("artist")).findElements(By.tagName("a"));
        String[] genres = driver.findElement(By.className("genre")).getText().substring(4).split("/");
        String description = driver.findElement(By.id("product-synopsis")).getText().substring(3);
        String imageUrl = driver.findElement(By.className("thumbnail")).getAttribute("src");
        log.info("title : "+ title);
        log.info("설명 : " + description);
        log.info("이미지 주소 : " + imageUrl);
        for(WebElement a : authors)
            log.info("author : " + a.getText());
        for(String g : genres)
            log.info("genre : " + g);

        WebElement element = driver.findElement(By.id("comic-episode-list"));
        List<WebElement> li  = element.findElements(By.tagName("li"));

        log.info("list 사이즈 : "+ li.size());
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Date day = null;
        Date now = new Date();
        int index = 0;
        WebElement webElement = li.get(0);

        for(int i = 1 ; i <li.size(); i++){
            WebElement w = li.get(i);
            try{
                day = format.parse(w.findElement(By.className("free-date")).getText());
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if(day.after(now)){
                webElement = li.get(i-1);
                index = i-1;
                break;
            }

        }
        String updatedDate = webElement.findElement(By.className("free-date")).getText();
        String count = webElement.findElement(By.className("episode-name")).getText();
        log.info("updatedDate " + updatedDate);
        log.info("count "+count);

    }
}
