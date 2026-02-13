package com.cdy.cdy.common.logging;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request,1024*1024);

        filterChain.doFilter(wrappedRequest, response);

        String url = wrappedRequest.getRequestURI();
        String method = wrappedRequest.getMethod();
        String body = new String(
                wrappedRequest.getContentAsByteArray(),
                StandardCharsets.UTF_8
        );

        log.trace("[{}] {} body={}", method, url, body);

    }
}
