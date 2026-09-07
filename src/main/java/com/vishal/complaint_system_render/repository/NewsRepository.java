package com.vishal.complaint_system_render.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.complaint_system_render.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
