package com.vishal.complaint_system_render.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.complaint_system_render.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByPhone(String phone);
}

