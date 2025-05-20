package com.project.arebbus.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.arebbus.model.Post;

public interface PostRepository extends JpaRepository<Post, Long>{
    
}
