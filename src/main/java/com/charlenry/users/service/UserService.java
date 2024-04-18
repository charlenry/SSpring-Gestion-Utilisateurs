package com.charlenry.users.service;

import java.util.List;

import com.charlenry.users.entities.Role;
import com.charlenry.users.entities.User;

public interface UserService {
	User saveUser(User user);
	User findUserByUsername (String username);
	Role addRole(Role role);
	User addRoleToUser(String username, String rolename);
	List<User> findAllUsers();
}
