package com.project.picktoon.controller.api;

import com.project.picktoon.domain.Platform;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.util.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/platforms")
@RequiredArgsConstructor
public class PlatformApiController {
    private final PlatformService platformService;

    @GetMapping("/{platformId}")
    public ResponseEntity<Platform> getPlatform(@PathVariable("platformId")int platformId){
        Platform platform = platformService.getPlatformById(platformId);
        if(platform == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(platform, HttpStatus.OK);
    }
//
//    @PostMapping
//    public ResponseEntity<Platform> addPlatform(@RequestBody String platformName){
//        Platform platform = new Platform();
//        platform.setPlatformName(platformName);
//        platform = platformService.addPlatform(platform);
//        return new ResponseEntity<>(platform, HttpStatus.OK);
//    }

    @DeleteMapping("/{platformId}")
    public ResponseEntity deletePlatform(@PathVariable("platformId") int id){
        if(platformService.getPlatformById(id)==null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        platformService.deletePlatform(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
