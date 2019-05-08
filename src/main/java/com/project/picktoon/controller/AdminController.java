package com.project.picktoon.controller;

import com.project.picktoon.domain.Keyword;
import com.project.picktoon.domain.Platform;
import com.project.picktoon.domain.Webtoon;
import com.project.picktoon.domain.WebtoonImage;
import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonInfo;
import com.project.picktoon.dto.DaumWebtoonDto.DaumWebtoonList;
import com.project.picktoon.dto.LoadWebtoonLink;
import com.project.picktoon.service.KeywordService;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.service.WebtoonImageService;
import com.project.picktoon.service.WebtoonService;
import com.project.picktoon.util.KeywordType;
import com.project.picktoon.util.SeeAge;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Log
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final KeywordService keywordService;
    private final PlatformService platformService;
    private final WebtoonImageService webtoonImageService;
    private final WebtoonService webtoonService;




//    @GetMapping("/regist")
//    public String regist(Model model){
//        List<Keyword> days = keywordService.getKeywordsByType(KeywordType.KEYWORD_DAY);
//        List<Keyword> genres = keywordService.getKeywordsByType(KeywordType.KEYWORD_GENRE);
//        List<Keyword> keywords = keywordService.getKeywordsByType(KeywordType.KEYWORD_KEYWORD);
//        List<Platform> platforms = platformService.getAllPlatforms();
//
//        model.addAttribute("days", days);
//        model.addAttribute("genres", genres);
//        model.addAttribute("keywords", keywords);
//        model.addAttribute("platforms", platforms);
//
//        return "admin/regist";
//    }

    @GetMapping("/regist")
    public String regist(Model model){
        List<Keyword> days = keywordService.getKeywordsByType(KeywordType.KEYWORD_DAY);
        List<Keyword> genres = keywordService.getKeywordsByType(KeywordType.KEYWORD_GENRE);
        List<Keyword> keywords = keywordService.getKeywordsByType(KeywordType.KEYWORD_KEYWORD);
        List<Platform> platforms = platformService.getAllPlatforms();

        model.addAttribute("days", days);
        model.addAttribute("genres", genres);
        model.addAttribute("keywords", keywords);
        model.addAttribute("platforms", platforms);

        return "admin/registcrawling";
    }

    @PostMapping("/regist")
    public String regist(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "author") String[] authors,
            @RequestParam(name= "day") Long[] days,
            @RequestParam(name= "multicheckbox_genre[]") Long[] genres,
            @RequestParam(name= "multicheckbox_keyword[]") Long[] keywordIds,
            @RequestParam(name = "link") String link,
            @RequestParam(name = "count") String count,
            @RequestParam(name = "seeage") int seeage,
            @RequestParam(name = "platform") int platform,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "image") MultipartFile[] images,
            @RequestParam(name = "imgurl", required = false)String imgUrl,
            @RequestParam(name = "updatedDate")String updatedDateStr
    ){
        Assert.hasText(title, "제목을 입력하세요.");
        Assert.notEmpty(authors, "작가를 입력하세요.");
        Assert.hasText(link, "링크를 입력하세요.");

        Webtoon webtoon = new Webtoon();
        webtoon.setTitle(title);
        webtoon.setLink(link);
        // 네이버는 링크와 크롤링링크가 같다.
        webtoon.setCrawlingLink(link);
        webtoon.setTotalCount(count);
        webtoon.setSeeAge(SeeAge.SEEAGES[seeage]);
        webtoon.setPlatform(platformService.getPlatformById(platform));
        webtoon.setDescription(description);

        List<Keyword> keywords = new ArrayList<>();

        // 작가
        for(String a : authors){
            if(a.length() == 0) // 작가 2가 없는 경우..
                continue;
            Keyword existAuthor = keywordService.getAuthorByName(a);
            if(existAuthor == null){
                Keyword author = new Keyword();
                author.setKeywordType(KeywordType.KEYWORD_AUTHOR);
                author.setKeywordValue(a);
                author = keywordService.addKeyword(author);
                keywords.add(author);
            }else{
                keywords.add(existAuthor);
            }

        }

        if(days != null)
            keywords.addAll(returnKeywords(days));
        if(genres != null)
            keywords.addAll(returnKeywords(genres));
        if(keywordIds != null)
            keywords.addAll(returnKeywords(keywordIds));

        webtoon.setKeywords(keywords);

        if(imgUrl == null) {
            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        WebtoonImage imageFile = new WebtoonImage();
                        imageFile.setLength(image.getSize());
                        imageFile.setMimeType(image.getContentType());
                        imageFile.setName(image.getOriginalFilename());
                        // 파일 저장
                        // /tmp/2019/2/12/123421-12341234-12341234-123423142
                        String saveFileName = saveFile(image);

                        imageFile.setSaveFileName(saveFileName); // save되는 파일명
                        webtoon.addWebtoonImage(imageFile);
                    }
                }
            }
        }else{
            WebtoonImage imageFile = saveFileFromUrl(imgUrl, title, platformService.getPlatformById(platform).getPlatformName());
            imageFile.setName(title);
            imageFile.setMimeType("image/jpeg");

            webtoon.addWebtoonImage(imageFile);
        }
        // 업데이트 날짜
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy.MM.dd");
        try {
            System.out.println(updatedDateStr);
            Date updateDate = transFormat.parse(updatedDateStr);
            webtoon.setUpdatedDate(updateDate);
        }catch (java.text.ParseException ex){
            ex.printStackTrace();
        }
        webtoonService.addWebtoon(webtoon);
        return "redirect:/main";
    }

    //    @PostMapping("/regist/daum")
//    public String registDaumWebtoon(@RequestBody LoadWebtoonLink loadWebtoonLink){
//        //http://webtoon.daum.net/data/pc/webtoon/view/homemaker
//        RestTemplate restTemplate = new RestTemplate();
//        DaumWebtoonList result = restTemplate.getForObject(loadWebtoonLink.getLink() , DaumWebtoonList.class);
//        log.info("size : "+result.getData().size());
//        List<DaumWebtoonInfo> webtoonInfos = result.getData();
//
//        return "redirect:/main";
//    }
    @GetMapping("/regist/daum")
    public String registDaumWebtoons(Model model){
        List<Keyword> days = keywordService.getKeywordsByType(KeywordType.KEYWORD_DAY);
        model.addAttribute("days", days);
        return "admin/registDaum";
    }


    private String saveFile(MultipartFile image){
        String dir = "imagefile/webtoon/";
        Calendar calendar = Calendar.getInstance();
        dir = dir + calendar.get(Calendar.YEAR);
        dir = dir + "/";
        dir = dir + (calendar.get(Calendar.MONTH) + 1);
        dir = dir + "/";
        dir = dir + calendar.get(Calendar.DAY_OF_MONTH);
        dir = dir + "/";
        File dirFile = new File(dir);
        dirFile.mkdirs(); // 디렉토리가 없을 경우 만든다. 퍼미션이 없으면 생성안될 수 있다.
        dir = dir + UUID.randomUUID().toString();

        try(FileOutputStream fos = new FileOutputStream(dir);
            InputStream in = image.getInputStream()
        ){
            byte[] buffer = new byte[1024];
            int readCount = 0;
            while((readCount = in.read(buffer)) != -1){
                fos.write(buffer, 0, readCount);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return dir;
    }
    // 크롤링한 이미지 저장. -- private으로 수정해야됌..
    public WebtoonImage saveFileFromUrl(String url, String title, String platform){
        String dir = "imagefile/webtoon/";
        WebtoonImage webtoonImage = new WebtoonImage();
        Calendar calendar = Calendar.getInstance();
        dir = dir + calendar.get(Calendar.YEAR);
        dir = dir + "/";
        dir = dir + (calendar.get(Calendar.MONTH) + 1);
        dir = dir + "/";
        dir = dir + calendar.get(Calendar.DAY_OF_MONTH);
        dir = dir + "/";
        dir = dir + platform + "/"; // 플랫폼 디렉토리..
        File dirFile = new File(dir);
        dirFile.mkdirs(); // 디렉토리가 없을 경우 만든다. 퍼미션이 없으면 생성안될 수 있다.
        dir = dir + title;
        try{
            URL imgUrl = new URL(url);
            BufferedImage jpg = ImageIO.read(imgUrl);
            File file = new File(dir+".jpg");
            ImageIO.write(jpg, "jpg", file);
            System.out.println("file length : "+file.length());

            webtoonImage.setSaveFileName(dir+".jpg");
            webtoonImage.setLength(file.length());

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return webtoonImage;
    }

    private List<Keyword> returnKeywords(Long[] ids){
        List<Keyword> result = new ArrayList<>();
        for(Long id: ids){
            result.add(keywordService.getKeywordById(id));
        }
        return result;
    }

    private void makeThumbnail(String filePath, String fileName, String fileExt) throws Exception {
        // 저장된 원본파일로부터 BufferedImage 객체를 생성합니다.
        BufferedImage srcImg = ImageIO.read(new File(filePath));
        // 썸네일의 너비와 높이 입니다.
        int dw = 150;
        int dh = 150;
        // 원본 이미지의 너비와 높이 입니다. 
        int ow = srcImg.getWidth();
        int oh = srcImg.getHeight();
        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow;
        int nh = (ow * dh)/dw;
        // 계산된 높이가 원본보다 높다면 crop이 안되므로 /
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if(nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }
        // 계산된 크기로 원본이미지를 가운데에서 crop 합니다. 
        BufferedImage cropImg = Scalr.crop(srcImg, (ow-nw)/2, (oh-nh)/2, nw, nh);
        // crop된 이미지로 썸네일을 생성합니다.
        BufferedImage destImg = Scalr.resize(cropImg, dw, dh);
        // 썸네일을 저장합니다. 이미지 이름 앞에 "THUMB_" 를 붙여 표시했습니다.
        String thumbName = "path" + "THUMB_" + fileName;
        File thumbFile = new File(thumbName);
        ImageIO.write(destImg, fileExt.toUpperCase(), thumbFile);
    }

    @GetMapping("registNewWebtoon")
    public String newwebtoon() {  return "/admin/registNewWebtoon"; }

}