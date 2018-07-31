package com.navitas.rfad.controller;

import com.navitas.rfad.bean.User;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController extends BaseController {
  /**
   * Gets the user profile information. constructor.
   *
   * @param email user's email
   */
  @GetMapping(value = "/profile/{email}")
  public ResponseEntity<User> getUser(@PathVariable final String email) {
    if (email == null) {
      return ResponseEntity.notFound().build();
    }

    final User user = new User();
    user.setUserId(UUID.randomUUID());
    user.setEmail(email);
    user.setFirstName("John");
    user.setLastName("Doe");

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}
