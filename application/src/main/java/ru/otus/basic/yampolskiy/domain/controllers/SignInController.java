package ru.otus.basic.yampolskiy.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.entities.UserLoginDTO;
import ru.otus.basic.yampolskiy.domain.services.UserService;
import ru.otus.basic.yampolskiy.servlets.annotations.PostRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.RequestBody;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.servlets.exceptions.AuthorizationException;
import ru.otus.basic.yampolskiy.servlets.models.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletResponse;
import ru.otus.basic.yampolskiy.servlets.utils.BearerAuthentication;
import ru.otus.basic.yampolskiy.servlets.utils.JwtUtils;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.*;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/signin")
public class SignInController extends HttpServlet {
    private ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    private static final Logger logger = LogManager.getLogger(SignInController.class);
    private UserService userService = UserService.getUserService();

    @PostRoute()
    public HttpServletResponse signIn(HttpServletRequest request, @RequestBody UserLoginDTO userLoginDTO) throws Exception {
        logger.debug("Проверка статуса в SignInController сокета строка 34: " + (request.getHttpRequest().getSocket().isClosed() ? "закрыт" : "открыт"));

        User existUser = userService.getUserByLogin(userLoginDTO.getLogin());
        if (existUser == null || !existUser.getPassword().equals(userLoginDTO.getPassword())) {
            throw new AuthorizationException("Неверно указан логин или пароль.");
        }

        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/octet-stream")
                .addHeader(HttpHeader.AUTHORIZATION, addToken(existUser))
                .setBody("")
                .build();
    }

    private String addToken(User user) {
        String jwtToken = JwtUtils.createToken(user.getLogin());
        BearerAuthentication bearerAuth = new BearerAuthentication(jwtToken);
        return bearerAuth.getJwtToken();
    }
}
