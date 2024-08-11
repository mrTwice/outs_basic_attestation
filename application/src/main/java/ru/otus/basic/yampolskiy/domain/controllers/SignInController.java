package ru.otus.basic.yampolskiy.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.entities.UserLoginDTO;
import ru.otus.basic.yampolskiy.domain.services.UserService;
import ru.otus.basic.yampolskiy.servlets.*;
import ru.otus.basic.yampolskiy.servlets.annotations.PostRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.servlets.security.BearerAuthentication;
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
    public HttpServletResponse signIn(HttpServletRequest request) throws Exception {
        String userLoginDTO = request.getBody();
        UserLoginDTO user = objectMapper.readValue(userLoginDTO, UserLoginDTO.class);
        User existUser = userService.getUserByUserName(user.getLogin());
        if (existUser == null || !existUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Неверно указан логин или пароль.");
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
