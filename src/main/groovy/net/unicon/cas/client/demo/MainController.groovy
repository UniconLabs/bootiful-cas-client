package net.unicon.cas.client.demo

import org.jasig.cas.client.boot.configuration.EnableCasClient
import org.jasig.cas.client.boot.configuration.CasClientConfigurer
import org.jasig.cas.client.authentication.AttributePrincipal
import org.jose4j.jwa.AlgorithmConstraints
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers
import org.jose4j.jwe.JsonWebEncryption
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jws.AlgorithmIdentifiers
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.InvalidJwtException
import org.jose4j.jwt.consumer.JwtConsumer
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.jwt.consumer.JwtContext
import org.jose4j.keys.AesKey
import org.jose4j.keys.RsaKeyUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAKey

@Controller
@EnableCasClient
class MainController implements CasClientConfigurer {

    @Value('${casLogoutUrl}')
    private String casLogoutUrl;

    String getCasLogoutUrl() {
        return casLogoutUrl
    }

    @Autowired
    RestTemplate restTemplate

    @Autowired
    PublicKey signingPublicKey

    @Autowired
    PrivateKey decryptionPrivateKey

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def index(HttpServletRequest request, HttpServletResponse response) {
        'index'
    }

    @RequestMapping(value = '/protected', method = RequestMethod.GET)
    def protected1(HttpServletRequest request, Model model) {
        String jwt = request.getParameter("ticket")
        //decryptJwt2(jwt)
        JwtClaims jwtClaims = decryptJwt(jwt)
        //AttributePrincipal principal = request.userPrincipal as AttributePrincipal
        //def respFromRestApi = restTemplate.getForObject("https://dk.example.org:8444/data", String)
        //model.addAttribute('jwt', jwt)
        //model.addAttribute("principal", principal)
        //model.addAttribute('dataFromRestApi', respFromRestApi)
        model.addAttribute('jwtClaims', jwtClaims.getClaimsMap())

        'protected-jwt'
    }

    private def decryptJwt(String jwt) {
        final String signingKey = "XZ4Iz7QkdRLPTJ6V1EYjXpgXbpXdZ3uixHOQ4AJVwyr6kkzqxmWCJhjEJiPaOGDqwsDHIGNP5AfEyGOGpOmSmQ"
        final String encryptionKey = "9OytxHyMtfEs09Hitzfixmb3JWoFqnKYGKr0wgjeYJ4"


        final Key key = new AesKey(signingKey.getBytes(StandardCharsets.UTF_8))

        final JsonWebSignature jws = new JsonWebSignature()
        jws.setCompactSerialization(jwt)
        jws.setKey(key)
        if (!jws.verifySignature()) {
            throw new Exception("JWT verification failed")
        }

        final byte[] decodedBytes = Base64.getDecoder().decode(jws.getEncodedPayload().getBytes(StandardCharsets.UTF_8))
        final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8)

        final JsonWebEncryption jwe = new JsonWebEncryption()
        final JsonWebKey jsonWebKey = JsonWebKey.Factory
                .newJwk("\n" + "{\"kty\":\"oct\",\n" + " \"k\":\"" + encryptionKey + "\"\n" + "}")

        jwe.setCompactSerialization(decodedPayload)
        jwe.setKey(new AesKey(jsonWebKey.getKey().getEncoded()))

        JwtClaims.parse(jwe.getPayload())
    }

    private def decryptJwt2(String jwt) {
        AlgorithmConstraints jwsAlgConstraints = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256)

        AlgorithmConstraints jweAlgConstraints = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                KeyManagementAlgorithmIdentifiers.ECDH_ES_A128KW)

        AlgorithmConstraints jweEncConstraints = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256)

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                //.setMaxFutureValidityInMinutes(300) // but the  expiration time can't be too crazy
                .setRequireSubject() // the JWT must have a subject claim
                //.setExpectedIssuer("sender") // whom the JWT needs to have been issued by
                //.setExpectedAudience("receiver") // to whom the JWT is intended for
                .setDecryptionKey(decryptionPrivateKey) // decrypt with the receiver's private key
                .setVerificationKey(signingPublicKey) // verify the signature with the sender's public key
                //.setJwsAlgorithmConstraints(jwsAlgConstraints) // limits the acceptable signature algorithm(s)
                //.setJweAlgorithmConstraints(jweAlgConstraints) // limits acceptable encryption key establishment algorithm(s)
                //.setJweContentEncryptionAlgorithmConstraints(jweEncConstraints) // limits acceptable content encryption algorithm(s)
                .build() // create the JwtConsumer instance

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt)
            System.out.println("JWT validation succeeded! " + jwtClaims)
        }
        catch (InvalidJwtException e)
        {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            System.out.println("Invalid JWT! " + e)
        }
    }


    /**
     * Example of customizing the filter config for any 'exotic' properties that are not exposed via properties file
     */
    @Override
    void configureValidationFilter(FilterRegistrationBean validationFilter) {
        //This is Groovy. Below this, is the example (commented out) on how to do it in Java lang.
        validationFilter.initParameters.millisBetweenCleanUps = '120000'
        //validationFilter.getInitParameters().put("millisBetweenCleanUps", "120000");
    }

    /**
     * Example of customizing the filter config for any 'exotic' properties that are not exposed via properties file
     */
    /*@Override
    void configureAuthenticationFilter(FilterRegistrationBean authenticationFilter) {
        //This is Groovy. Below this, is the example (commented out) on how to do it in Java lang.
        authenticationFilter.initParameters.artifactParameterName = 'casTicket'
        authenticationFilter.initParameters.serviceParameterName = 'targetService'

        //authenticationFilter.getInitParameters().put("artifactParameterName", "casTicket");
        //authenticationFilter.getInitParameters().put("serviceParameterName", "targetService");
    }*/
}
