package com.scm.services;

import com.scm.entities.User;
import java.util.*;

public interface UserService {
    //Creating methods

    User saveUser(User user);

    Optional<User> getUserById(String id);

    Optional<User> updateUser(User user);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User>getAllUsers();

    User getUserByEmail(String email);

    // NEW: Register user and send verification email
     void registerUserWithVerification(User user, String siteURL);


}
