package ib.project.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ib.project.dto.UserDTO;
import ib.project.model.Authority;
import ib.project.model.User;
import ib.project.service.AuthorityServiceInterface;
import ib.project.service.UserServiceInterface;

@RestController
@RequestMapping(value = "api/users")
public class UserController {
	
	private UserServiceInterface userService;
	private AuthorityServiceInterface authorityService;
	PasswordEncoder passwordEncoder;
	
	public UserController(UserServiceInterface userService, AuthorityServiceInterface authorityService, PasswordEncoder passwordEncoder){
		this.userService = userService;
		this.authorityService = authorityService;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping("/user/all")
	public List<User> getAll() {
		return this.userService.findAll();
	}
	
	@GetMapping(value = "/active-users")
	public ResponseEntity<List<UserDTO>> getActive(){
		List<UserDTO> active = new ArrayList<>();
		List<User> users = userService.findByActiveTrue();
		for (User user : users) {
			active.add(new UserDTO(user));
		}
		return new ResponseEntity<>(active,HttpStatus.OK);
	}
	
	@GetMapping(value = "/inactive-users")
	public ResponseEntity<List<UserDTO>>getInactive(){
		List<UserDTO> inactive = new ArrayList<>();
		List<User> users = userService.findByActiveFalse();
		for (User user : users) {
			inactive.add(new UserDTO(user));
		}
		return new ResponseEntity<>(inactive,HttpStatus.OK);
	}
	
	// User registration	
	@PostMapping(value="/register", consumes="application/json")
	public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {		
		Authority authority = authorityService.findByName("REGULAR");		
		User u = userService.findByEmail(userDTO.getEmail());
		if(u!=null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}		
		
		u = new User();
		u.setEmail(userDTO.getEmail());
		u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		// pri registraciji je user inicijalno false dok ga admin ne odobri
		u.setActive(false);
		u.getUserAuthorities().add(authority);		

		u = userService.save(u);
		return new ResponseEntity<>(new UserDTO(u),HttpStatus.OK);
	}
	
	// User activation
	@PutMapping(value="/activate/{id}")
	public ResponseEntity<UserDTO> activateUser(@PathVariable("id") Long id){
		User user = userService.findById(id);
		if(user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		user.setActive(true);
		user = userService.save(user);
		return new ResponseEntity<>(new UserDTO(user),HttpStatus.OK);
	}
	
}
