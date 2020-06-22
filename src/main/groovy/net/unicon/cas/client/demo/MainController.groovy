package net.unicon.cas.client.demo

import org.jasig.cas.client.boot.configuration.EnableCasClient
import org.jasig.cas.client.boot.configuration.CasClientConfigurer
import org.jasig.cas.client.authentication.AttributePrincipal
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

    @RequestMapping(value = '/', method = RequestMethod.GET)
    def index(HttpServletRequest request, HttpServletResponse response) {
        'index'
    }

    @RequestMapping(value = '/protected', method = RequestMethod.GET)
    def protected1(HttpServletRequest request, Model model) {
        AttributePrincipal principal = request.userPrincipal as AttributePrincipal
        def pt = principal.getProxyTicketFor("https://dk.example.org:8444/data")
        def urlEncodedPt = URLEncoder.encode(pt, 'UTF-8')
        def respFromRestApi = restTemplate.getForObject("https://dk.example.org:8444/data?ticket=$urlEncodedPt", String)
        model.addAttribute('principal', principal)
        model.addAttribute('dataFromRestApi', respFromRestApi)



        'protected'
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
