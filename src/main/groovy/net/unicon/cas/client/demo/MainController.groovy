package net.unicon.cas.client.demo

import org.jasig.cas.client.boot.configuration.EnableCasClient
import org.jasig.cas.client.boot.configuration.CasClientConfigurer
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.keys.AesKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets
import java.security.Key

@Controller
@EnableCasClient
class MainController implements CasClientConfigurer {

    @Value('${casLogoutUrl}')
    String casLogoutUrl

    //Set as OS env variable CAS_JWT_SIGNING_SECRET
    @Value('${CAS_JWT_SIGNING_SECRET}')
    String signingSecret

    //Set as OS env variable CAS_JWT_ENCRYPTION_SECRET
    @Value('${CAS_JWT_ENCRYPTION_SECRET}')
    String encryptionSecret

    @Autowired
    RestTemplate restTemplate

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def index(HttpServletRequest request, HttpServletResponse response) {
        'index'
    }

    @RequestMapping(value = '/protected', method = RequestMethod.GET)
    def protected1(HttpServletRequest request, Model model) {
        String jwt = request.getParameter("ticket")
        JwtClaims jwtClaims = decryptJwt(jwt)
        model.addAttribute('principal', jwtClaims.getClaimsMap().get('sub'))
        model.addAttribute('jwtClaims', jwtClaims.getClaimsMap())

        'protected'
    }

    private def decryptJwt(String jwt) {
        //First Signature verification
        final Key signingKey = new AesKey(signingSecret.getBytes(StandardCharsets.UTF_8))
        final JsonWebSignature jws = new JsonWebSignature()
        jws.setCompactSerialization(jwt)
        jws.setKey(signingKey)
        if (!jws.verifySignature()) {
            throw new Exception("JWT verification failed")
        }

        //Then get encrypted payload
        final byte[] decodedBytes = Base64.getDecoder().decode(jws.getEncodedPayload().getBytes(StandardCharsets.UTF_8))
        final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8)

        //Finally decrypt into JWT claims
        final JsonWebEncryption jwe = new JsonWebEncryption()
        final JsonWebKey jsonWebKey = JsonWebKey.Factory.newJwk(Map.of('kty', 'oct', 'k', encryptionSecret))
        jwe.setCompactSerialization(decodedPayload)
        jwe.setKey(new AesKey(jsonWebKey.getKey().getEncoded()))
        JwtClaims.parse(jwe.getPayload())
    }
}
