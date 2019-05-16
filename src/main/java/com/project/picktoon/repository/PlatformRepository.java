package com.project.picktoon.repository;

import com.project.picktoon.domain.Platform;
import com.project.picktoon.util.PlatformType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlatformRepository extends JpaRepository<Platform, Integer> {
    public Platform findByPlatformName(PlatformType name);
}
