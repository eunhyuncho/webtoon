package com.project.picktoon.controller.api;

import com.project.picktoon.domain.NewWebtoon;
import com.project.picktoon.dto.NewWebtoonDto;
import com.project.picktoon.service.NewWebtoonService;
import com.project.picktoon.service.WebtoonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/newwebtoons")
@RequiredArgsConstructor
public class NewWebtoonApiController {
    private final NewWebtoonService newWebtoonService;
    private final WebtoonService webtoonService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<NewWebtoonDto>> getNewWebtoon(){
        List<NewWebtoon> newWebtoonlist = newWebtoonService.getNewWebtoons();
        List<NewWebtoonDto> newWebtoons = new ArrayList<>();

            for(NewWebtoon newWebtoon : newWebtoonlist){
                NewWebtoonDto newWebtoonDto = modelMapper.map(newWebtoon, NewWebtoonDto.class);
                //TODO 이미지 추가하고 테스트하기
//            newWebtoonDto.setWebtoonImageId(newWebtoon.getWebtoon().getWebtoonImage().getId());
                newWebtoons.add(newWebtoonDto);
            }
        return new ResponseEntity<>(newWebtoons, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity updateNewWebtoon(@RequestBody NewWebtoonDto updateNewWebtoon){
        newWebtoonService.updateNewWebtoon(updateNewWebtoon.getId(), updateNewWebtoon.getWebtoonId(), updateNewWebtoon.getOrdering());
        return new ResponseEntity(HttpStatus.OK);
    }


}
