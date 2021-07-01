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
		User u = userRepository.findByEmail(email);
		return u;
	}

	@Override
	public List<User> findAll() {
		List<User> result = userRepository.findAll();
		return result;
	}

	@Override
	public List<User> findByActiveTrue() {
		List<User> activeU = userRepository.findByActiveTrue();
		return activeU;
	}

	@Override
	public List<User> findByActiveFalse() {
		List<User> inactiveU = userRepository.findByActiveFalse();
		return inactiveU;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
}
