package com.project.picktoon.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.picktoon.domain.Platform;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.util.PlatformType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {PlatformApiController.class}, includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
public class PlatformApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private PlatformService platformService;

    @Test
    public void 플랫폼_불러오기() throws Exception {

        Platform platform = Platform.builder()
                .id(5)
                .platformName(PlatformType.Daum)
                .build();

        Mockito.when(platformService.getPlatformById(5)).thenReturn(platform);

        mockMvc.perform(get("/api/platforms/5")
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8) //응답요청이 있을때
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

//    @Test
//    public void 플랫폼_추가하기() throws Exception{
//
//        String platformName = "카카오";
//
//        Platform platform = Platform.builder()
//                .platformName(platformName)
//                .build();
//
//        Mockito.when(platformService.addPlatform(platform)).thenReturn(platform);
//
//        mockMvc.perform(post("/api/platforms")
//                .with(csrf())
//                .contentType(MediaType.APPLICATION_JSON_UTF8) //requestbody
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .content(objectMapper.writeValueAsString(platform))
//        )
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.jsonPath("platformName").value("카카오"))
//                .andExpect(status().isOk());
//    }

    @Test
    public void 플랫폼_삭제하기() throws Exception{

        int deletePaltform = 4;

        Platform platform = Platform.builder()
                .build();

        Mockito.when(platformService.getPlatformById(deletePaltform)).thenReturn(platform);

        mockMvc.perform(delete("/api/platforms/4")
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

}