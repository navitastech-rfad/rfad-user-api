package com.navitas.rfad.filters;

import com.navitas.rfad.model.entity.Person;
import com.navitas.rfad.model.repository.PersonRepository;

import io.jsonwebtoken.Jwts;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

  private PersonRepository personRepository;

  @Value("${security.jwt.client-secret:SecretKeyToGenJWTs}")
  private String secret;

  /**
   * JwtAuthorizationFilter constructor.
   *
   * @param authenticationManager used to perform authentication of credentials
   * @param personRepository repository interface to query person data
   */
  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager, PersonRepository personRepository) {
    super(authenticationManager);
    this.personRepository = personRepository;
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(JwtAuthenticationFilter.HEADER_STRING);
    final String cookie = request.getHeader(JwtAuthenticationFilter.HEADER_COOKIE);
    final String fingerprint = getFingerprint(cookie);

    if (fingerprint != null) {
      String fingerprintHash;
      try {
        fingerprintHash = JwtAuthenticationFilter.createFingerprintHash(fingerprint);
      } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
        throw new RuntimeException("Invalid Token Fingerprint Hash");
      }

      if (token != null) {
        // parse the token with fingerprint hash
        // verification per OWASP standards
        final String email =
            Jwts.parser()
                .require(JwtAuthenticationFilter.FINGERPRINT_HASH, fingerprintHash)
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token.replace(JwtAuthenticationFilter.TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        final Person parsedUser = personRepository.findByEmail(email);
        final List<GrantedAuthority> authorityList =
            parsedUser
                .getPersonRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getCode()))
                .collect(Collectors.toList());
        LOGGER.debug("-------Adding user with role---------" + authorityList.get(0).getAuthority());

        if (email != null) {
          return new UsernamePasswordAuthenticationToken(email, null, authorityList);
        }

        return null;
      }
    }
    return null;
  }

  private String getFingerprint(String cookie) {
    if (cookie != null) {
      final String[] cookies = cookie.split(";");

      for (final String str : cookies) {
        final String[] keyValue = str.trim().split("=", 2);

        if (keyValue.length == 2 && keyValue[0].equals(JwtAuthenticationFilter.FINGERPRINT)) {
          return keyValue[1];
        }
      }
    }

    return null;
  }
}
