package ib.project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ib.project.model.User;
import ib.project.repository.UserRepository;
import ib.project.service.UserServiceInterface;

@Service
public class UserService implements UserServiceInterface {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findById(Long id) {
		User user = userRepository.getOne(id);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public List<User> findByActiveTrue() {
		return userRepository.findByActiveTrue();
	}

	@Override
	public List<User> findByActiveFalse() {
		return userRepository.findByActiveFalse();
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
}
