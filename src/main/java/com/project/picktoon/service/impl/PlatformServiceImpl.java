package com.project.picktoon.service.impl;

import com.project.picktoon.domain.Platform;
import com.project.picktoon.repository.PlatformRepository;
import com.project.picktoon.service.PlatformService;
import com.project.picktoon.util.PlatformType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformServiceImpl implements PlatformService {
    public final PlatformRepository platformRepository;

    @Override
    @Transactional(readOnly = true)
    public Platform getPlatformById(int id) {
        return platformRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Platform addPlatform(Platform platform) {
        return platformRepository.save(platform);
    }

    @Override
    @Transactional
    public void deletePlatform(int id) {
        platformRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Platform getPlatformByPlatformName(PlatformType name) {
        return platformRepository.findByPlatformName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }
}
