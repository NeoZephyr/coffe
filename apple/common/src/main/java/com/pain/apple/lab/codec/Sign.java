package com.pain.apple.lab.codec;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sign {

    private static final Map<String, JWTVerifier> verifierMap = new HashMap<>();
    private static final Map<String, Algorithm> algorithmMap = new HashMap<>();

    private static Algorithm getAlgorithm(String token) {
        Algorithm algorithm = algorithmMap.get(token);

        if (algorithm == null) {
            synchronized (algorithmMap) {
                algorithm = algorithmMap.get(token);

                if (algorithm == null) {
                    algorithm = Algorithm.HMAC512(token);
                    algorithmMap.put(token, algorithm);
                }
            }
        }
        return algorithm;
    }

    public static String generateSessionToken(String userId, String signToken, long duration) {
        if (StringUtils.isEmpty(signToken)) {
            throw new RuntimeException("No signing token present");
        }
        Algorithm algorithm = getAlgorithm(signToken);
        return JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .sign(algorithm);
    }

    static DecodedJWT verifyToken(String tokenString, String signingToken) {
        JWTVerifier verifier = verifierMap.get(signingToken);

        if (verifier == null) {
            synchronized (verifierMap) {
                verifier = verifierMap.get(signingToken);

                if (verifier == null) {
                    Algorithm algorithm = Algorithm.HMAC512(signingToken);
                    verifier = JWT.require(algorithm).build();
                    verifierMap.put(signingToken, verifier);
                }
            }
        }

        return verifier.verify(tokenString);
    }
}
