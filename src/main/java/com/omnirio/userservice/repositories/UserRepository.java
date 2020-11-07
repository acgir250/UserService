package com.omnirio.userservice.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omnirio.userservice.entities.User;

@Repository
public interface  UserRepository extends JpaRepository<User, Long>{

   
   public Optional<User> findByUserName(String userName);
   public List<User> findByGender(String gender);
   public List<User> findByPhoneNumber(String phoneNumber);
   
}
