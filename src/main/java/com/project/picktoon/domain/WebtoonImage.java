package com.project.picktoon.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Calendar;


@Entity
@Table(name = "webtoon_image")
@Setter
@Getter
@ToString
public class WebtoonImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String name;

    @Column(length = 45)
    private String mimeType;

    @Column
    private Long length;

    @Column(length = 255)
    private String saveFileName;

    @ManyToOne
    @JoinColumn(name = "webtoon_id")
    private Webtoon webtoon;

    public WebtoonImage(){

    }

    public WebtoonImage(String url, String title, String platform){
        String dir = "imagefile/webtoon/";
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

            this.setSaveFileName(dir+".jpg");
            this.setLength(file.length());
            this.setName(title);
            this.setMimeType("image/jpeg");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
