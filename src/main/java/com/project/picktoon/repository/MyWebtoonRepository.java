package com.project.picktoon.repository;

import com.project.picktoon.domain.MyWebtoon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyWebtoonRepository extends JpaRepository<MyWebtoon, Long> {

    //오름차순
    @Query("SELECT m FROM MyWebtoon m INNER JOIN FETCH m.webtoon WHERE m.userId = :userId ORDER BY m.webtoon.title")
    public List<MyWebtoon> getMyWebToonsByTitleAsc(@Param("userId")Long userId);

    //내림차순
    @Query("SELECT m FROM MyWebtoon m INNER JOIN FETCH m.webtoon WHERE m.userId = :userId ORDER BY m.webtoon.title DESC")
    public List<MyWebtoon> getMyWebToonsByTitleDesc(@Param("userId")Long userId);

    //업데이트 오름차순 (오래된순)
    @Query("SELECT m FROM MyWebtoon m INNER JOIN FETCH m.webtoon WHERE m.userId = :userId ORDER BY m.webtoon.webtoonState.updatedDate")
    public List<MyWebtoon> getMyWebToonsByOldest(@Param("userId")Long userId);

    //업데이트 내림차순 (최신순)
    @Query("SELECT m FROM MyWebtoon m INNER JOIN FETCH m.webtoon WHERE m.userId = :userId ORDER BY m.webtoon.webtoonState.updatedDate DESC")
    public List<MyWebtoon> getMyWebToonsByUpdate(@Param("userId")Long userID);
}