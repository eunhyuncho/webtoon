package com.project.picktoon.controller;

import com.project.picktoon.domain.WebtoonImage;
import com.project.picktoon.service.WebtoonImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
@RequestMapping("/webtoons")
@RequiredArgsConstructor
public class WebtoonController {
    private final WebtoonImageService webtoonImageService;

    @GetMapping("/searchform")
    public String search(){
        return "webtoons/search";
    }

    @GetMapping("/search")
    public String searchWebtoon() { return "webtoons/searchlist";}

    @GetMapping("/mywebtoons")
    public String myWebtoon() { return "webtoons/mywebtoons";}

    @GetMapping("/{webtoonId}")
    public String webtoonDetails() { return "webtoons/details";}


    @GetMapping("/images/{id}")
    @ResponseBody // 컨트롤러안에서 직접 response를 이용하여 결과를 출력할 때 사용
    public void downloadImage(
            @PathVariable(name = "id") Long id,
            HttpServletResponse response
    ) {
        WebtoonImage imageFile = webtoonImageService.getWebtoonImage(id);
        response.setContentType(imageFile.getMimeType());

        try(FileInputStream fis = new FileInputStream(imageFile.getSaveFileName());
            OutputStream out = response.getOutputStream()
        ){
            byte[] buffer = new byte[2048];
            int readCount = 0;

            while((readCount = fis.read(buffer)) != -1){
                out.write(buffer, 0, readCount);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}
