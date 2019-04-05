package com.project.picktoon.service.impl;

import com.project.picktoon.domain.Keyword;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.dto.SearchKeyword;
import com.project.picktoon.repository.WebtoonRepository;
import com.project.picktoon.service.WebtoonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebtoonServiceImpl implements WebtoonService {
    public final WebtoonRepository webtoonRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Webtoon> getWebtoons(List<SearchKeyword> keywords, String searchStr, int page) {
        //검색 결과 한 페이지 당 10개의 웹툰을 보여준다.
        int limit = 10;
        int start = page * limit - limit;
        return webtoonRepository.getWebtoons(keywords, searchStr, start, limit);
    }


    @Override
    @Transactional(readOnly = true)
    public Webtoon getWebtoonById(Long id) {
        return webtoonRepository.getWebtoon(id);
    }

    @Override
    @Transactional
    public Webtoon addWebtoon(Webtoon webtoon) {
        return webtoonRepository.save(webtoon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Webtoon> getBestWebtoons() {
        return webtoonRepository.getBestWebtoons();
    }

    @Override
    @Transactional
    public void updateWebtoon(Webtoon webtoon){
        // 식별자가 존재하면 병합을 수행한다.
        webtoonRepository.save(webtoon);
    }

    @Override
    @Transactional
    public void deleteWebtoon(Long id) {
        webtoonRepository.deleteWebtoonById(id);
    }
}
