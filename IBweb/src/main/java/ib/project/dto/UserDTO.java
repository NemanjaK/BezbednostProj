package ib.project.dto;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ib.project.model.Authority;
import ib.project.model.User;

public class UserDTO {

	private Long id;
	private String email;
	private String password;
	private String certificate;
	private boolean active;
	private Set<String> userAuthorities = new HashSet();

	public UserDTO() {

	}

	public UserDTO(Long id, String email, String password, String certificate, boolean active,
			Set<Authority> userAuthority) {
		super();
		this.id = id;
		this.email = email;
		// this.password = password;
		this.certificate = certificate;
		this.active = active;
		for (Authority authority : userAuthority) {
			this.userAuthorities.add(authority.getName());
		}
	}

	public UserDTO(User user) {
		this(user.getId(), user.getEmail(), user.getPassword(), user.getCertificate(), user.isActive(),
				user.getUserAuthorities());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<String> getUserAuthorities() {
		return userAuthorities;
	}

	public void setUserAuthorities(Set<String> userAuthorities) {
		this.userAuthorities = userAuthorities;
	}

}
