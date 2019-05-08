package com.project.picktoon.service;

import com.project.picktoon.domain.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlatformServiceTest {
    @Autowired
    PlatformService platformService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void 플랫폼_플랫폼아이디로검색(){
        Platform platform = platformService.getPlatformById(1);
        System.out.println(platform);
    }

//    @Test
//    public void 플랫폼_플랫폼추가하기(){
//        Platform platform = new Platform();
//        platform.setPlatformName(P);
//        platformService.addPlatform(platform);
//        System.out.println(platform);
//    }

    @Test
    public void 플랫폼_플랫폼삭제하기() throws Exception{
        platformService.deletePlatform(3);
    }

}

