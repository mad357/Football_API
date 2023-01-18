package org.football.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotAuthorizedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    private final String apiUrl;

    @Inject
    public UserService(@ConfigProperty(name = "application.url") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Transactional
    public String login(String login, String password) {
        User user = userRepository.find( "login = ?1 and password = ?2", login, password).firstResult();

        if (user != null) {
            Map<String, Object> headerClaims = new HashMap<>();
            Map<String, Object> payload = new HashMap<>();
            payload.put("login", user.getLogin());
            long time = java.time.Instant.now().getEpochSecond();
            payload.put("iat", time);
            payload.put("exp", time + 7200);
            payload.put("nbf", 0);

            payload.put("iss", apiUrl);

            List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            payload.put("roles", roles);
            try {
                Path path = Paths.get("src/main/resources/football_api_private_key.der");
                byte[] dataPrivate = Files.readAllBytes(path);
                path = Paths.get("src/main/resources/football_api_public_key.der");
                byte[] dataPublic = Files.readAllBytes(path);

                KeyFactory kf = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(dataPrivate);
                RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);
                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(dataPublic);
                RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

                Algorithm algorithm = Algorithm.RSA256( pubKey, privKey);
                String token = JWT.create()
                        .withHeader(headerClaims)
                        .withPayload(payload)
                        .sign(algorithm);

                String result = new JSONObject()
                        .put("token", token)
                        .toString();

                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new NotAuthorizedException("Invalid login or password");
    }
}
