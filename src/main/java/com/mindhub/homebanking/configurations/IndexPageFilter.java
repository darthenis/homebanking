package com.mindhub.homebanking.configurations;

import org.apache.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IndexPageFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if (isAuthenticated() && "/web/index.html".equals(servletRequest.getRequestURI())) {

            String encodedRedirectURL;

            if(isAdmin()){

                encodedRedirectURL = ((HttpServletResponse) response).encodeRedirectURL(
                        servletRequest.getContextPath() + "/admin/manager.html");

            } else {

                encodedRedirectURL = ((HttpServletResponse) response).encodeRedirectURL(
                        servletRequest.getContextPath() + "/web/accounts.html");

            }

            servletResponse.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
            servletResponse.setHeader("Location", encodedRedirectURL);
        }

        chain.doFilter(servletRequest, servletResponse);

    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }

        return authentication.isAuthenticated();
    }

    private boolean isAdmin(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream().anyMatch(autority -> autority.getAuthority().equals("ADMIN"));

    }
}
