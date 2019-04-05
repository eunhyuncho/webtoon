package com.project.picktoon.controller.api;

import com.project.picktoon.domain.NewWebtoon;
import com.project.picktoon.dto.NewWebtoonDto;
import com.project.picktoon.service.NewWebtoonService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/newwebtoon")
@RequiredArgsConstructor
public class NewWebtoonApiController {
    private final NewWebtoonService newWebtoonService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<NewWebtoonDto> getNewWebtoon(){
        List<NewWebtoon> newWebtoonlist = newWebtoonService.getNewWebtoons();
        List<NewWebtoonDto> newWebtoons = new ArrayList<>();

            for(NewWebtoon newWebtoon : newWebtoonlist){
                NewWebtoonDto newWebtoonDto = modelMapper.map(newWebtoon, NewWebtoonDto.class);
                //TODO 이미지 추가하고 테스트하기
//            newWebtoonDto.setWebtoonImageId(newWebtoon.getWebtoon().getWebtoonImage().getId());
                newWebtoons.add(newWebtoonDto);
            }
        return newWebtoons;
    }

    @PutMapping
    public void updateNewWebtoon(@RequestBody NewWebtoonDto updateNewWebtoon){
        newWebtoonService.updateNewWebtoon(updateNewWebtoon.getId(), updateNewWebtoon.getWebtoonId(), updateNewWebtoon.getOrdering());
    }
}
