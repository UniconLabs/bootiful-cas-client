package net.unicon.cas.client.demo

import net.unicon.cas.client.configuration.EnableCasClient
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@EnableCasClient
class MainController {

    @RequestMapping(value = '/', method = RequestMethod.GET)
    void index(HttpServletRequest request, HttpServletResponse response, Model model) {
        AttributePrincipal principal = request.userPrincipal
        response.writer.println("You are logged in to CAS as [${principal}]")
    }

}
