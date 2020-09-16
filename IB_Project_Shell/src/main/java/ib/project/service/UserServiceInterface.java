package ib.project.service;

import java.util.List;

import ib.project.model.User;

public interface UserServiceInterface {
	User findByEmail(String username);
	User findByEmailAndPassword (String email, String password);
	List<User> findAll();
	User save(User user);
}
