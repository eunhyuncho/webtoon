package com.project.picktoon.service;

import com.project.picktoon.domain.Platform;
import com.project.picktoon.util.PlatformType;

import java.util.List;

public interface PlatformService {
    public Platform getPlatformById(int id);
    public Platform getPlatformByPlatformName(PlatformType name);
    public Platform addPlatform(Platform platform);
    public void deletePlatform(int id);
    public List<Platform> getAllPlatforms();
}
