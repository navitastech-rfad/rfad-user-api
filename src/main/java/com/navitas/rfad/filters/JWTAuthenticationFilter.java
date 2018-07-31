package com.navitas.rfad.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navitas.rfad.bean.Credentials;
import com.navitas.rfad.model.entity.Person;
import com.navitas.rfad.model.repository.PersonRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private static final Random RANDOM = new SecureRandom();

  protected static final long EXPIRATION_TIME = 864_000_000; // 10 days
  protected static final String TOKEN_PREFIX = "Bearer ";
  protected static final String HEADER_STRING = "Authorization";
  protected static final String HEADER_COOKIE = "Set-Cookie";
  protected static final String FINGERPRINT = "__Secure-Fgp";
  protected static final String FINGERPRINT_HASH = "userFingerprint";
  protected static final String ALGORITHM = "SHA-256";

  private AuthenticationManager authenticationManager;
  private PersonRepository personRepository;
  private ObjectMapper objectMapper;

  @Value("${security.jwt.client-secret}")
  private String signingKey = "SecretKeyToGenJWTs";

  /**
   * JwtAuthenticationFilter constructor.
   *
   * @param authenticationManager used to perform authentication of credentials
   * @param personRepository repository interface to query person data
   * @param objectMapper json object mapper
   */
  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager,
      PersonRepository personRepository,
      ObjectMapper objectMapper) {
    this.authenticationManager = authenticationManager;
    this.personRepository = personRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    try {
      final Credentials creds =
          new ObjectMapper().readValue(request.getReader(), Credentials.class);
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              creds.getUsername(), creds.getPassword(), new ArrayList<>()));
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException, ServletException {
    try {
      final String fingerprint = createFingerprint();
      final String fingerprintHash = createFingerprintHash(fingerprint);
      final String email = ((User) authResult.getPrincipal()).getUsername();
      final Person user = personRepository.findByEmail(email);
      final String jwtToken =
          Jwts.builder()
              .setSubject(email)
              .setIssuer("navitas-rfad")
              .setAudience("com.navitas.rfad")
              .claim("userId", user.getId().toString())
              .claim("email", user.getEmail())
              .claim("firstName", user.getFirstName())
              .claim("lastName", user.getLastName())
              .claim("role", user.getPersonRoles().iterator().next().getRole().getCode())
              .claim(FINGERPRINT_HASH, fingerprintHash)
              .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
              .signWith(SignatureAlgorithm.HS512, signingKey.getBytes())
              .compact();

      response.setStatus(HttpStatus.OK.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.addHeader(HEADER_STRING, TOKEN_PREFIX + jwtToken);
      response.setHeader(HEADER_COOKIE, createCookieString(fingerprint));

      final Map<String, String> tokenMap = new HashMap<>();
      tokenMap.put("token", jwtToken);
      tokenMap.put("userId", user.getId().toString());
      tokenMap.put("firstName", user.getFirstName());
      tokenMap.put("lastName", user.getLastName());
      tokenMap.put("email", user.getEmail());
      tokenMap.put("role", user.getPersonRoles().iterator().next().getRole().getCode());

      objectMapper.writeValue(response.getWriter(), tokenMap);
    } catch (final NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private String createCookieString(String fingerprint) {
    final StringBuilder builder = new StringBuilder();
    builder.append(FINGERPRINT).append("=").append(fingerprint).append("; ");
    builder.append("SameSite=strict; ");
    builder.append("HttpOnly; ");
    builder.append("Secure");
    return builder.toString();
  }

  private String createFingerprint() {
    final byte[] randomFgp = new byte[50];
    RANDOM.nextBytes(randomFgp);
    return DatatypeConverter.printHexBinary(randomFgp);
  }

  protected static String createFingerprintHash(String fingerprint)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
    final byte[] userFingerprintDigest = digest.digest(fingerprint.getBytes("utf-8"));
    return DatatypeConverter.printHexBinary(userFingerprintDigest);
  }
}
