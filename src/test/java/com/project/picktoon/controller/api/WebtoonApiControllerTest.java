package com.project.picktoon.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.picktoon.config.SecurityConfig;
import com.project.picktoon.config.WebConfig;
import com.project.picktoon.domain.Keyword;
import com.project.picktoon.domain.Platform;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.KeywordDto;
import com.project.picktoon.dto.WebtoonForm;
import com.project.picktoon.service.*;
import com.project.picktoon.util.PlatformType;
import com.project.picktoon.util.SeeAge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {WebtoonApiController.class}, includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class})) // includeFilters를 포함하지 않아도 Spring Security설정이 잘 사용된다. (?)
public class WebtoonApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    WebtoonService webtoonService;
    @MockBean
    KeywordService keywordService;
    @MockBean
    PlatformService platformService;
    @MockBean
    WebtoonImageService webtoonImageService;
    @Autowired
    ModelMapper modelMapper;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void 웹툰_추가() throws Exception{
        KeywordDto keywordDto1 = KeywordDto.builder().id(1L).keywordType(1).keywordValue("월").build();
        KeywordDto keywordDto2 = KeywordDto.builder().id(3L).keywordType(3).keywordValue("수").build();
        List<KeywordDto> keywords = new ArrayList<>();
        List<Keyword> keywords1 = new ArrayList<>();
        keywords.add(keywordDto1);
        keywords.add(keywordDto2);


        Keyword keyword1 = Keyword.builder().id(1L).keywordType(keywordDto1.getKeywordType())
                                            .keywordValue(keywordDto1.getKeywordValue()).ordering(1).build();
        Keyword keyword2 = Keyword.builder().id(3L).keywordType(keywordDto1.getKeywordType())
                                            .keywordValue(keywordDto2.getKeywordValue()).ordering(2).build();

        keywords1.add(keyword1);
        keywords1.add(keyword2);

        Platform platform = Platform.builder().id(1).platformName(PlatformType.Naver).build();

        WebtoonForm webtoonForm = WebtoonForm.builder()
                                            .title("웹툰 제목")
                                            .link("www.naver.com")
                                            .description("어쩌구 저쩌구")
                                            .seeAge(SeeAge.SEEAGE_12)
                                            .state("연재중")
                                            .keywords(keywords)
                                            .platformId(1)
                                            .subscription(0)
                                            .updatedDate(new Date())
                                            .updateState(false)
                                            .totalCount("32화")
                                            .build();


//        Webtoon webtoon = Webtoon.builder()
//                            .id(null)
//                            .title(webtoonForm.getTitle())
//                            .description(webtoonForm.getDescription())
//                            .link(webtoonForm.getLink())
//                            .seeAge(webtoonForm.getSeeAge())
//                            .state(webtoonForm.getState())
//                            .platform(platform)
//                            .subscription(0)
//                            .updateState(false)
//                            .totalCount("32화")
//                            .keywords(keywords1)
//                            .updatedDate(webtoonForm.getUpdatedDate())
//                            .build();

        Webtoon webtoon = modelMapper.map(webtoonForm, Webtoon.class);
        webtoon.setPlatform(platform);

        Mockito.when(webtoonService.addWebtoon(webtoon)).thenReturn(webtoon);
        Mockito.when(keywordService.getKeywordById(1L)).thenReturn(keyword1);
        Mockito.when(keywordService.getKeywordById(3L)).thenReturn(keyword2);
        Mockito.when(platformService.getPlatformById(1)).thenReturn(platform);

        //when —> 테스트 메소드
        mockMvc.perform(post("/api/webtoons")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(webtoonForm))
        )
                .andDo(print()) // 값을 출력할 수 있다.
                .andExpect(status().isCreated());
        //then —> 테스트 결과값.
    }

    @Test
    public void 웹툰_조회() throws Exception{
        Platform platform = Platform.builder().id(1).platformName(PlatformType.Naver).build();
        Keyword keyword1 = Keyword.builder().id(1L).keywordType(1)
                .keywordValue("월").ordering(1).build();
        Keyword keyword2 = Keyword.builder().id(3L).keywordType(1)
                .keywordValue("수").ordering(2).build();

        List<Keyword> keywords = new ArrayList<>();
        keywords.add(keyword1);
        keywords.add(keyword2);

        Webtoon webtoon = Webtoon.builder()
                            .title("1번 웹툰").platform(platform)
                            .state("연재중").description("웹툰 상세 설명...").id(1L).keywords(keywords).build();

        Mockito.when(webtoonService.getWebtoonById(1L)).thenReturn(webtoon);

        mockMvc.perform(get("/api/webtoons/1").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andDo(print());
    }



}