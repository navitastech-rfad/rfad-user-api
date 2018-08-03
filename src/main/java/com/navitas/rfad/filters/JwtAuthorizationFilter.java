package com.navitas.rfad.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
  private PublicKey verifierKey;

  /**
   * JwtAuthorizationFilter constructor.
   *
   * @param authenticationManager used to perform authentication of credentials
   * @param personRepository repository interface to query person data
   */
  public JwtAuthorizationFilter(
      AuthenticationManager authenticationManager, String verifierKeyStr) {
    super(authenticationManager);
    this.verifierKey = JwtHelper.getPublicKey(verifierKeyStr.trim());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    final String header = req.getHeader(JwtAuthenticationFilter.HEADER_STRING);

    if (header == null || !header.startsWith(JwtAuthenticationFilter.TOKEN_PREFIX)) {
      chain.doFilter(req, res);
      return;
    }

    final UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }

  @SuppressWarnings("unchecked")
  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(JwtAuthenticationFilter.HEADER_STRING);
    
    if (token != null) {
      final Claims claims =
          Jwts.parser()
              .setSigningKey(verifierKey)
              .parseClaimsJws(token.replace(JwtAuthenticationFilter.TOKEN_PREFIX, ""))
              .getBody();
      final String username = claims.get("preferred_username", String.class);
      final Map<?, ?> realmAccess = claims.get("realm_access", Map.class);
      final List<String> roles = (List<String>) realmAccess.get("roles");
      final List<GrantedAuthority> authorityList =
          roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

      if (username != null) {
        return new UsernamePasswordAuthenticationToken(username, null, authorityList);
      }

      return null;
    }

    return null;
  }
}
