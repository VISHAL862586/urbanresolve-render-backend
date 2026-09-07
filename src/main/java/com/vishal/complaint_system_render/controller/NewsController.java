package com.vishal.complaint_system_render.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.complaint_system_render.entity.News;
import com.vishal.complaint_system_render.repository.NewsRepository;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository repo;

    // GET ALL NEWS
    @GetMapping
    public List<News> getAllNews() {
        return repo.findAll();
    }

    // ADD NEWS
    @PostMapping
    public News addNews(@RequestBody News news) {

        return repo.save(news);
    }

    // DELETE NEWS
    @DeleteMapping("/{id}")
    public String deleteNews(@PathVariable Long id) {

        repo.deleteById(id);

        return "News deleted successfully";
    }
}