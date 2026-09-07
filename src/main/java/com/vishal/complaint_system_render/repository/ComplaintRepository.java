package com.vishal.complaint_system_render.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vishal.complaint_system_render.entity.Complaint;
import com.vishal.complaint_system_render.entity.User;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
List<Complaint> findByUser(User user);
}
