package com.navitas.rfad.filters;

import static org.springframework.security.jwt.codec.Codecs.b64Decode;
import static org.springframework.security.jwt.codec.Codecs.utf8Encode;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);
  private static Pattern PUBLIC_KEY =
      Pattern.compile("-----BEGIN (.*)-----(.*)-----END (.*)-----", Pattern.DOTALL);

  public static RSAPublicKey getPublicKey(String keyValue) {
    final Matcher m = PUBLIC_KEY.matcher(keyValue.trim());

    if (!m.matches()) {
      throw new IllegalArgumentException("String is not a valid public key encoded data");
    }

    try {
      final String type = m.group(1);
      final String key = m.group(2);
      //      final byte[] content = Base64.getDecoder().decode(m.group(2));
      final byte[] content = b64Decode(utf8Encode(m.group(2)));
      final KeyFactory fact = KeyFactory.getInstance("RSA");
      final KeySpec keySpec = new X509EncodedKeySpec(content);
      return (RSAPublicKey) fact.generatePublic(keySpec);
    } catch (final NoSuchAlgorithmException e) {
      LOGGER.error("Invalid algorithm", e);
      throw new IllegalStateException(e);
    } catch (final InvalidKeySpecException e) {
      LOGGER.error("Invalid key specification", e);
      throw new RuntimeException(e);
    }
  }
}
