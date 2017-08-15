package net.unicon.cas.client.demo

import net.unicon.cas.client.configuration.CasClientConfigurerAdapter
import net.unicon.cas.client.configuration.EnableCasClient
import org.apereo.cas.util.EncodingUtils
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.crypto.Cipher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.security.PrivateKey

@Controller
@EnableCasClient
class MainController extends CasClientConfigurerAdapter {

    @Value('${casLogoutUrl}')
    private String casLogoutUrl

    @Autowired
    private PrivateKey credentialPrivateKey

    String getCasLogoutUrl() {
        return casLogoutUrl
    }

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def index(HttpServletRequest request, HttpServletResponse response, Model model) {
        'index'
    }

    @RequestMapping(value = '/protected', method = RequestMethod.GET)
    def protectedEndpoint(HttpServletRequest request, Model model) {
        AttributePrincipal principal = request.userPrincipal

        //Decrypt incoming encrypted password and swap out its value in the attributes map to pass it to a view for display
        //Not a solid security practice, just done here to demonstrate the mechanics of it, should such a need arise
        principal.attributes.credential = decryptPassword(principal.attributes.credential)

        model.addAttribute('principal', principal)
        'protected'
    }

    /**
     * Password decryption dance
     */
    private String decryptPassword(String encryptedPassword) {
        final Cipher cipher = Cipher.getInstance(this.credentialPrivateKey.algorithm)
        final byte[] cred64 = EncodingUtils.decodeBase64(encryptedPassword)
        cipher.init(Cipher.DECRYPT_MODE, this.credentialPrivateKey)
        new String(cipher.doFinal(cred64))
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
