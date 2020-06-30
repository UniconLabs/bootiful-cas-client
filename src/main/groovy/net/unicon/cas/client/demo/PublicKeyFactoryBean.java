package net.unicon.cas.client.demo;

import groovy.transform.ToString;
import groovy.util.logging.Slf4j;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import javax.crypto.Cipher;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


public class PublicKeyFactoryBean extends AbstractFactoryBean<PublicKey> {

    private static final Logger logger = LoggerFactory.getLogger(PublicKeyFactoryBean.class);

    private final Resource resource;
    private final String algorithm;

    public PublicKeyFactoryBean(String algorithm, Resource resource) {
        this.algorithm = algorithm;
        this.resource = resource;
    }

    /**
     * Initialize cipher based on service public key.
     *
     * @return the false if no public key is found
     * or if cipher cannot be initialized, etc.
     */
    public Cipher toCipher() {
        try {
            PublicKey publicKey = getObject();
            if (publicKey != null) {
                Cipher cipher = Cipher.getInstance(this.algorithm);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return cipher;
            }
        } catch (final Exception e) {
            logger.warn("Cipher could not be initialized. Error [{}]", e.getMessage());
        }
        return null;
    }

    @Override
    public Class getObjectType() {
        return PublicKey.class;
    }

    @Override
    protected PublicKey createInstance() throws Exception {
        var key = readPemPublicKey();
        if (key == null) {
            logger.debug("Key [{}] is not in PEM format. Trying next...", this.resource);
            key = readDERPublicKey();
        }
        return key;
    }

    /**
     * Read pem public key.
     *
     * @return the public key
     * @throws Exception the exception
     */
    protected PublicKey readPemPublicKey() throws Exception {
        try (var reader = new PemReader(new InputStreamReader(this.resource.getInputStream(), StandardCharsets.UTF_8))) {
            var pemObject = reader.readPemObject();
            if (pemObject != null) {
                var content = pemObject.getContent();
                var pubSpec = new X509EncodedKeySpec(content);
                var factory = KeyFactory.getInstance(this.algorithm);
                return factory.generatePublic(pubSpec);
            }
        }
        return null;
    }

    /**
     * Read der public key.
     *
     * @return the public key
     * @throws Exception the exception
     */
    protected PublicKey readDERPublicKey() throws Exception {
        logger.debug("Creating public key instance from [{}] using [{}]", this.resource.getFilename(), this.algorithm);
        try (var pubKey = this.resource.getInputStream()) {
            var bytes = new byte[(int) this.resource.contentLength()];
            pubKey.read(bytes);
            var pubSpec = new X509EncodedKeySpec(bytes);
            var factory = KeyFactory.getInstance(this.algorithm);
            return factory.generatePublic(pubSpec);
        }
    }
}
