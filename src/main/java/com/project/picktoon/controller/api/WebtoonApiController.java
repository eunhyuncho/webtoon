package com.project.picktoon.controller.api;

import com.project.picktoon.domain.Platform;
import com.project.picktoon.domain.User;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.*;
import com.project.picktoon.service.*;
import com.project.picktoon.util.PlatformType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
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
    private final MyWebtoonService myWebtoonService;
    private final UserService userService;

    // 웹툰 id로 가져오기 (상세정보 포함)
    @GetMapping("/{webtoonId}")
    public ResponseEntity<WebtoonDetailDto> getWebtoon(@PathVariable Long webtoonId, Principal principal){
        Webtoon webtoon = webtoonService.getWebtoonById(webtoonId);
        if(webtoon == null)
            return new ResponseEntity<WebtoonDetailDto>(HttpStatus.NO_CONTENT);

        WebtoonDetailDto webtoonDetailDto = modelMapper.map(webtoon, WebtoonDetailDto.class);
        // 웹툰 이미지
        if(!webtoon.getWebtoonImages().isEmpty())
            webtoonDetailDto.setWebtoonImageId(webtoon.getWebtoonImages().get(0).getId());
        if(principal == null){  //비회원..
            webtoonDetailDto.setUserId(-1L);
            webtoonDetailDto.setMyWebtoon(false);
        }else{
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            webtoonDetailDto.setUserId(user.getId());
            webtoonDetailDto.setMywebtoonId(myWebtoonService.getMyWebtoon(user.getId(), webtoonId));
            webtoonDetailDto.setMyWebtoon(myWebtoonService.checkMyWebtoon(user.getId(), webtoonId));
        }
        return new ResponseEntity<>(webtoonDetailDto, HttpStatus.OK);
    }
    //베스트웹툰 가져오기
    @GetMapping("/bestWebtoons")
    public ResponseEntity<List<WebtoonDto>> getBestWebtoon(){
        List<Webtoon> webtoonlist = webtoonService.getBestWebtoons();
        return getListWebtoonDto(webtoonlist);
    }

    //List<WebtoonDto> 만드는 메소드 추출
    public ResponseEntity<List<WebtoonDto>> getListWebtoonDto(List<Webtoon> webtoonlist) {
        List<WebtoonDto> webtoons = new ArrayList<>();

        for (Webtoon webtoon : webtoonlist) {
            WebtoonDto webtoonDto = modelMapper.map(webtoon, WebtoonDto.class);
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

    //웹툰 한개만 가져오기
    @PostMapping("/registNew")
    public ResponseEntity<NewWebtoonDto> addNewWebtoon(@RequestBody AddNewWebtoonDto addNewWebtoonDto){
        NewWebtoonDto newWebtoonDto = new NewWebtoonDto();

        if(webtoonService.getWebtoonByTitle(addNewWebtoonDto.getWebtoonTitle())!=null) {
            Webtoon webtoon = webtoonService.getWebtoonByTitle(addNewWebtoonDto.getWebtoonTitle());
            newWebtoonDto.setWebtoonTitle(addNewWebtoonDto.getWebtoonTitle()); //웹툰제목
            newWebtoonDto.setWebtoonId(webtoon.getId()); //웹툰아이디
            newWebtoonDto.setId(addNewWebtoonDto.getId());//아이디
            newWebtoonDto.setOrdering(addNewWebtoonDto.getOrdering());//ordering
            if (!webtoon.getWebtoonImages().isEmpty())
                newWebtoonDto.setWebtoonImageId(webtoon.getWebtoonImages().get(0).getId()); //웹툰이미지
        }
        //TODO 값이 없을 떄 오류처리 해주기
//        else{
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
        return new ResponseEntity<>(newWebtoonDto, HttpStatus.OK);
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