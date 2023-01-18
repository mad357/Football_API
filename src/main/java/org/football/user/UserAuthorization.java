package org.football.user;

import annotations.AllowedRoles;
import annotations.Authorized;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Interceptor
@Authorized
public class UserAuthorization {

    private final String apiUrl;

    @Context
    HttpHeaders headers;

    @Inject
    public UserAuthorization(@ConfigProperty(name = "application.url") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @AroundInvoke
    public Object authorizeInterceptor(InvocationContext ic) throws Exception {
        String tokenString = headers.getHeaderString("Authorization");
        if (tokenString == null || tokenString.isEmpty()) {
            throw new NotAuthorizedException("User is not logged in");
        }
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
        JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(apiUrl)
        .build();
        try {
            String[] allowedRoles = ic.getMethod().getAnnotation(AllowedRoles.class).value();
            DecodedJWT jwt = verifier.verify(tokenString.substring(7));
            Map<String, Claim> claims = jwt.getClaims();
            List<String> assignedRoles = claims.get("roles").asList(String.class);
            if (Arrays.stream(allowedRoles).noneMatch(assignedRoles::contains)) {
                throw new ForbiddenException("User cannot perform this operation");
            }
        }
        catch (JWTVerificationException e) {
            throw new NotAuthorizedException("Invalid token signature");
        }

        return ic.proceed();
    }
}
