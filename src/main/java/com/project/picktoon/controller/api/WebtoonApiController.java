package com.project.picktoon.controller.api;

import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.*;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.service.WebtoonImageService;
import com.project.picktoon.service.WebtoonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/webtoons")
@RequiredArgsConstructor
public class WebtoonApiController {

    private final WebtoonService webtoonService;
    private final WebtoonImageService webtoonImageService;
    private final PlatformService platformService;
    private final ModelMapper modelMapper;

    // 웹툰 id로 가져오기 (상세정보 포함)
    @GetMapping("/{webtoonId}")
    public ResponseEntity<WebtoonDto> getWebtoon(@PathVariable Long webtoonId){
        Webtoon webtoon = webtoonService.getWebtoonById(webtoonId);
        if(webtoon == null)
            return new ResponseEntity<WebtoonDto>(HttpStatus.NO_CONTENT);

        WebtoonDto webtoonDto = modelMapper.map(webtoon, WebtoonDto.class);
        return new ResponseEntity<>(webtoonDto, HttpStatus.OK);
    }

    @GetMapping("/bestWebtoons")
    public ResponseEntity<List<WebtoonDto>> getBestWebtoon(){
        List<Webtoon> webtoonlist = webtoonService.getBestWebtoons();
        List<WebtoonDto> webtoons = new ArrayList<>();

        for(Webtoon webtoon : webtoonlist){
            WebtoonDto webtoonDto = modelMapper.map(webtoon, WebtoonDto.class);
            //TODO 이미지 추가하고 테스트하기
            webtoons.add(webtoonDto);
        }
        return new ResponseEntity<>(webtoons, HttpStatus.OK);
    }

    // 웹툰 추가하기
    @PostMapping
    public ResponseEntity<WebtoonDto> addWebtoon(@Valid @RequestBody WebtoonForm webtoonForm, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Webtoon webtoon = modelMapper.map(webtoonForm, Webtoon.class);
        webtoon.setId(null);
        webtoon.setPlatform(platformService.getPlatformById(webtoonForm.getPlatformId()));
        webtoon = webtoonService.addWebtoon(webtoon);
        WebtoonDto webtoonDto = modelMapper.map(webtoon, WebtoonDto.class);
        return new ResponseEntity<>(webtoonDto, HttpStatus.CREATED);
    }

    //웹툰 수정하기
    @PutMapping
    public ResponseEntity<Result> updateWebtoon(@Valid @RequestBody WebtoonForm webtoonForm, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if(!webtoonService.existWebtoonById(webtoonForm.getId()))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Webtoon webtoon = modelMapper.map(webtoonForm, Webtoon.class);
        webtoon.setPlatform(platformService.getPlatformById(webtoonForm.getPlatformId()));
        webtoonService.updateWebtoon(webtoon);

        return new ResponseEntity<>(new Result("SUCCESS"), HttpStatus.OK);
    }

    //웹툰 삭제하기
    @DeleteMapping("/{webtoonId}")
    public ResponseEntity deleteWebtoon(@PathVariable Long webtoonId){
        if(!webtoonService.existWebtoonById(webtoonId))
            return new ResponseEntity(HttpStatus.NO_CONTENT);

        webtoonService.deleteWebtoon(webtoonId);
        return new ResponseEntity(HttpStatus.OK);
    }

    //웹툰 검색
    @GetMapping("/search")
    public ResponseEntity<List<SearchWebtoonDto>> searchWebtoons(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                           @RequestParam(name = "key1", required = false) String[] keywords1,
                                           @RequestParam(name = "key2", required = false) String[] keywords2,
                                           @RequestParam(name = "key3", required = false) String[] keywords3,
                                           @RequestParam(name = "key4", required = false) String[] keywords4,
                                           @RequestParam(name = "searchStr", required = false) String searchStr){
        List<SearchKeyword> searchKeywords = new ArrayList<>();

        if(keywords1 != null)
            searchKeywords.addAll(convertToSearchKeyword(1,keywords1));
        if(keywords2 != null)
            searchKeywords.addAll(convertToSearchKeyword(2,keywords2));
        if(keywords3 != null)
            searchKeywords.addAll(convertToSearchKeyword(3,keywords3));
        if(keywords4 != null)
            searchKeywords.addAll(convertToSearchKeyword(4,keywords4));

        List<Webtoon> webtoons =  webtoonService.getWebtoons(searchKeywords, searchStr, page);

        Type listType = new TypeToken<List<SearchWebtoonDto>>(){}.getType();
        List<SearchWebtoonDto> results = modelMapper.map(webtoons, listType);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    private List<SearchKeyword> convertToSearchKeyword(int keywordType, String[] keywords){
        List<SearchKeyword> result = new ArrayList<>();
        SearchKeyword searchKeyword = new SearchKeyword();
        for(String value : keywords){
            searchKeyword.setKeywordType(keywordType);
            searchKeyword.setKeywordValue(value);
            result.add(searchKeyword);
        }
        return result;
    }

}
