package com.project.arebbus.repositories;

import com.project.arebbus.model.PostTag;
import com.project.arebbus.model.PostTagId;
import com.project.arebbus.model.Post;
import com.project.arebbus.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {
    List<PostTag> findByPost(Post post);
    List<PostTag> findByTag(Tag tag);
    boolean existsByPostAndTag(Post post, Tag tag);
}
