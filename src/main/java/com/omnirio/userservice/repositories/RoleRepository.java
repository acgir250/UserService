package com.omnirio.userservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omnirio.userservice.entities.Role;
import com.omnirio.userservice.entities.User;

@Repository
public interface  RoleRepository extends JpaRepository<Role, Long>{

   
   public User findByRoleName(String userName);
   public List<User> findByRoleCode(String gender);
   
   
}
