package ib.project.rest;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.cert.X509CRLHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ib.project.certificate.model.IssuerData;
import ib.project.certificate.model.SubjectData;
import ib.project.certificate.utils.CRLManager;
import ib.project.certificate.utils.CertificateGenerator;
import ib.project.certificate.utils.KeyStoreCertificateGenerator;
import ib.project.dto.UserDTO;
import ib.project.keystore.KeyStoreWriter;
import ib.project.model.Authority;
import ib.project.model.User;
import ib.project.service.AuthorityServiceInterface;
import ib.project.service.UserServiceInterface;
import ib.project.service.impl.KeyStoreService;

@RestController
@RequestMapping(value = "api/users")
@CrossOrigin("*")
public class UserController {

	@Autowired
	private UserServiceInterface userService;

	@Autowired
	private AuthorityServiceInterface authorityService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	KeyStoreService keyStoreService;

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public List<User> getAll() {
		return this.userService.findAll();
	}

	@GetMapping(value = "/active-users")
	public ResponseEntity<List<UserDTO>> getActive() {
		List<UserDTO> active = new ArrayList<>();
		List<User> users = userService.findByActiveTrue();
		for (User user : users) {
			active.add(new UserDTO(user));
		}
		return new ResponseEntity<>(active, HttpStatus.OK);
	}

	@GetMapping(value = "/inactive-users")
	public ResponseEntity<List<UserDTO>> getInactive() {
		List<UserDTO> inactive = new ArrayList<>();
		List<User> users = userService.findByActiveFalse();
		for (User user : users) {
			inactive.add(new UserDTO(user));
		}
		return new ResponseEntity<>(inactive, HttpStatus.OK);
	}

	// User registration
	@PostMapping(value = "/register", consumes = "application/json")
	public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
		Authority authority = authorityService.findByName("ROLE_REGULAR");
		User u = userService.findByEmail(userDTO.getEmail());
		if (u != null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		// Kreiranje novog korisnika
		u = new User();
		u.setEmail(userDTO.getEmail());
		u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		// pri registraciji je user inicijalno false dok ga admin ne odobri
		u.setActive(false);
		u.getUserAuthorities().add(authority);

		u = userService.save(u);

		String email = u.getEmail();		
		String shortMail = email.substring(0, email.indexOf("@"));
		
		// Kreiranje KeyStore-a za korisnika
		if (keyStoreService.generateKeyStore(u)) {
			u.setCertificate("data/" + shortMail + ".jks");
		}

		return new ResponseEntity<>(new UserDTO(u), HttpStatus.OK);
	}

	// User activation
	@PutMapping(value = "/activate/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> activateUser(@PathVariable("id") Long id) {
		User user = userService.findById(id);
		if (user == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		user.setActive(true);
		user = userService.save(user);
		return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
	}

	@RequestMapping("/whoami")
	public UserDTO user(Principal user) {
		return new UserDTO(this.userService.findByEmail(user.getName()));
	}

	@GetMapping(value = "/whoami/download")
	public ResponseEntity<byte[]> download(Principal user) {
		User u = this.userService.findByEmail(user.getName());
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		Path userDir = Paths.get("data/" + u.getId() +"/keystore.jks");

		File file = null;
		try {
			file = new File(userDir.toUri().toURL().getFile());
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("filename", "keystore.jks");

		byte[] bFile = keyStoreService.readBytesFromFile(file.toString());

		return ResponseEntity.ok().headers(headers).body(bFile);
	}

}
