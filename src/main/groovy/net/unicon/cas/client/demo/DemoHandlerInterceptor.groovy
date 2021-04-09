package net.unicon.cas.client.demo

import groovy.util.logging.Slf4j
import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class DemoHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //Kind of an optimization. We only care about HandlerMethod which is any intercepted controller's method.
        //Could also check here for any specific controller method and short-circuit early, like here
        if(!(handler instanceof HandlerMethod)) {
            return true
        }
        def session = request.session
        if(!session.getAttribute('additional_data')) {
            AttributePrincipal p = request.userPrincipal
            session.setAttribute('additional_data', "${p.attributes.uid}: ADDITIONAL DATA")
        }
        return true
    }
}
