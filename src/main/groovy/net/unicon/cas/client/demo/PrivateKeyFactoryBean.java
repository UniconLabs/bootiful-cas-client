package net.unicon.cas.client.demo;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;


public class PrivateKeyFactoryBean extends AbstractFactoryBean<PrivateKey> {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private final Resource location;

    private final String algorithm;

    public PrivateKeyFactoryBean(String algorithm, Resource location) {
        this.algorithm = algorithm;
        this.location = location;
    }

    private static final Logger logger = LoggerFactory.getLogger(PrivateKeyFactoryBean.class);

    @Override
    protected PrivateKey createInstance() {
        var key = readPemPrivateKey();
        if (key == null) {
            logger.debug("Key [{}] is not in PEM format. Trying next...", this.location);
            key = readDERPrivateKey();
        }
        return key;
    }

    private PrivateKey readPemPrivateKey() {
        logger.trace("Attempting to read as PEM [{}]", this.location);
        try (Reader in = new InputStreamReader(this.location.getInputStream(), StandardCharsets.UTF_8);
             var br = new BufferedReader(in);
             var pp = new PEMParser(br)) {
            var pemKeyPair = (PEMKeyPair) pp.readObject();
            var kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
            return kp.getPrivate();
        } catch (final Exception e) {
            logger.debug("Unable to read key", e);
            return null;
        }
    }

    private PrivateKey readDERPrivateKey() {
        logger.debug("Attempting to read key as DER [{}]", this.location);
        try (var privKey = this.location.getInputStream()) {
            var bytes = new byte[(int) this.location.contentLength()];
            privKey.read(bytes);
            var privSpec = new PKCS8EncodedKeySpec(bytes);
            var factory = KeyFactory.getInstance(this.algorithm);
            return factory.generatePrivate(privSpec);
        } catch (final Exception e) {
            logger.debug("Unable to read key", e);
            return null;
        }
    }

    @Override
    public Class getObjectType() {
        return PrivateKey.class;
    }
}