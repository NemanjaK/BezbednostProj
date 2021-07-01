package ib.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ib.project.model.Authority;
import ib.project.repository.AuthorityRepository;
import ib.project.service.AuthorityServiceInterface;

@Service
public class AuthorityService implements AuthorityServiceInterface {

	@Autowired
	AuthorityRepository authorityRepository;
	
	@Override
	public Authority findByName(String name) {
		return authorityRepository.findByName(name);
	}

	
}
