package com.omnirio.userservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnirio.userservice.entities.User;
import com.omnirio.userservice.service.UserService;

@RestController
@RequestMapping("/api/userservice")
public class UserController {

    @Autowired
    UserService service;

    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody String user) throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
       @SuppressWarnings("unchecked")
    Map<String,Object> parser= mapper.readValue(user, Map.class);
        return ResponseEntity.ok(service.saveUser(parser));
    }

    @PutMapping("/updates/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User user,@PathVariable("id") long id) throws Exception {
        return ResponseEntity.ok(service.Update(user, id));
    }
  
    

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() throws Exception {
        return ResponseEntity.ok(service.getAllUser());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") int id) throws Exception {
        service.delete(id);
        return null;
    }
}
