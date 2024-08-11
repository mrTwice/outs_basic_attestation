package ru.otus.basic.yampolskiy.servlets.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.RequestContext;
import ru.otus.basic.yampolskiy.servlets.utils.JwtUtils;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;

public class JwtAuthenticationFilter implements Filter {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());

    @Override
    public boolean doFilter(HttpServletRequest request) throws Exception {
        return isAuthorized(request);
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String header = request.getHeader(HttpHeader.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            logger.debug("Отсутствует или неверный формат заголовка Authorization");
            return false;
        }
        String jwtToken = header.substring("Bearer ".length());
        if(!JwtUtils.isValidToken(jwtToken)){
            logger.debug("Токен не валидный");
            return false;
        }
        request.getRequestContext().setAttribute("username", JwtUtils.extractUsername(jwtToken));
        return true;
    }
}
