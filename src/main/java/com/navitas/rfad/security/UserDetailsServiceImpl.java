package com.navitas.rfad.security;

import com.navitas.rfad.model.entity.Person;
import com.navitas.rfad.model.entity.PersonRole;
import com.navitas.rfad.model.repository.PersonRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Named
public class UserDetailsServiceImpl implements UserDetailsService {
  @Inject private PersonRepository personRepo;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    final Person user = personRepo.findByEmail(email);

    if (user == null) {
      throw new UsernameNotFoundException(email);
    }

    return new User(user.getEmail(), user.getPassword(), getAuthorities(user.getPersonRoles()));
  }

  private List<GrantedAuthority> getAuthorities(Set<PersonRole> roles) {
    return roles
        .stream()
        .map(role -> new SimpleGrantedAuthority(role.getRole().getCode()))
        .collect(Collectors.toList());
  }
}
