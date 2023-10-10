package org.football.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.football.user.Passwords.isExpectedPassword;


@ApplicationScoped
public class UserService {
    @Inject
    UserRepository userRepository;

    private final String apiUrl;

    @ConfigProperty(name = "public.key.path")
    private String publicKeyPath;

    @ConfigProperty(name = "private.key.path")
    private String privateKeyPath;

    final ModelMapper modelMapper;

    @ConfigProperty(name = "password.pepper")
    private String pepper;

    @Inject
    public UserService(@ConfigProperty(name = "application.url") String apiUrl) {
        this.apiUrl = apiUrl;
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
            }
        });
    }

    @Transactional
    public void registerUser(UserDto userDto) throws NoSuchAlgorithmException {
        User alreadyExist = userRepository.find( "login = ?1 ", userDto.getLogin()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("Username already taken");
        }
        else {
            User user =  modelMapper.map(userDto, User.class);
            byte[] salt = Passwords.getNextSalt();
            byte[] hash = Passwords.hash((userDto.getPassword() + pepper).toCharArray(), salt);
            user.setSalt(salt);
            user.setPassword(hash);
            userRepository.persist(user);
        }
    }

    @Transactional
    public String login(String login, String password) {
        User user = userRepository.find( "login = ?1", login).firstResult();
        if (user != null && password != null && isExpectedPassword((password + pepper).toCharArray(), user.getSalt(), user.getPassword())) {

            JSONObject result = new JSONObject()
                    .put("access_token", generateAccessToken(user))
                    .put("refresh_token", generateRefreshToken(user));

            return result.toString();
        }
        throw new NotAuthorizedException(
                Response.status(Response.Status.UNAUTHORIZED)
                .entity("Invalid login or password")
                .build());
    }

    private String generateAccessToken(User user) {
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", "JWT");
        headerClaims.put("alg", "RS256");
        Map<String, Object> payload = new HashMap<>();
        payload.put("login", user.getLogin());
        long time = java.time.Instant.now().getEpochSecond();
        payload.put("iat", time);
        payload.put("exp", time + 10000);
        payload.put("nbf", 0);
        payload.put("typ", "Bearer");

        payload.put("iss", apiUrl);

        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        payload.put("roles", roles);
        try {
            Path path = Paths.get(privateKeyPath);
            byte[] dataPrivate = Files.readAllBytes(path);
            path = Paths.get(publicKeyPath);
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

           return token;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRefreshToken(User user) {
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("typ", "JWT");
        headerClaims.put("alg", "RS256");
        Map<String, Object> payload = new HashMap<>();
        payload.put("login", user.getLogin());
        long time = java.time.Instant.now().getEpochSecond();
        payload.put("iat", time);
        payload.put("exp", time + ( 86400 * 7 ));
        payload.put("nbf", 0);
        payload.put("typ", "Bearer");
        payload.put("iss", apiUrl);

        try {
            Path path = Paths.get(privateKeyPath);
            byte[] dataPrivate = Files.readAllBytes(path);
            path = Paths.get(publicKeyPath);
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

            return token;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String loginWithRefreshToken(String refreshToken){
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new NotAuthorizedException("Invalid refresh token");
            }
            Path path = Paths.get("src/main/resources/football_api_public_key.der");
            byte[] dataPublic = Files.readAllBytes(path);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(dataPublic);
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
            if (!verifyToken(refreshToken, pubKey)) {
                throw new NotAuthorizedException("Invalid token");
            }

            String[] parts = refreshToken.split("\\.");
            JSONObject payload = new JSONObject(decode(parts[1]));

            User user = userRepository.find( "login = ?1", payload.get("login")).firstResult();
            if (user != null) {
                JSONObject result = new JSONObject()
                        .put("access_token", generateAccessToken(user));

                return result.toString();
            }
            else {
                throw new RuntimeException("User not found");
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyToken(String token, RSAPublicKey publicKey){
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(apiUrl)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e){
            System.out.println("Exception in verifying " + e);
            return false;
        }
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
