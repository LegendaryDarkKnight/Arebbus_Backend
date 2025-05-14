package com.project.arebbus.repositories;

import com.project.arebbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
