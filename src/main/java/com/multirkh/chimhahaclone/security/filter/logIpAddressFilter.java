package com.multirkh.chimhahaclone.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class logIpAddressFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (null != csrfToken) {
            String actualToken = request.getHeader(csrfToken.getHeaderName());
            System.out.println("Client CSRF Token: " + actualToken);
        } else {
            System.out.println("Client CSRF Token: " + "No CSRF Token");
        }

        filterChain.doFilter(request, response);
    }
}