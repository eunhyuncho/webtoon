package com.project.picktoon.controller;

import com.project.picktoon.domain.WebtoonImage;
import com.project.picktoon.util.PlatformType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminControllerTest {

    @Autowired
    AdminController adminController;

    @Test
    public void 웹툰_이미지_저장_다음(){

        //WebtoonImage webtoonImage = adminController.saveFileFromUrl("http://t1.daumcdn.net/webtoon/op/3a34d4bed55da90bc97236f06cf900cf286d4199", "one", PlatformType.Daum.toString());
        //System.out.println("file name : " + webtoonImage.getSaveFileName());
    }


}