package ru.otus.basic.yampolskiy.domain.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.User;
import ru.otus.basic.yampolskiy.domain.entities.UserRegistrationDTO;
import ru.otus.basic.yampolskiy.domain.services.UserService;
import ru.otus.basic.yampolskiy.servlets.annotations.PostRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.RequestBody;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletResponse;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/signup")
public class SignUpController extends HttpServlet {
    private ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    private static final Logger logger = LogManager.getLogger(SignUpController.class);
    private final UserService userService = UserService.getUserService();

    @PostRoute()
    public HttpServletResponse signUp(HttpServletRequest request, @RequestBody UserRegistrationDTO user) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest is null");
        }
        // Логируем перед вызовом
        logger.debug("signUp: Получен HttpServletRequest: " + request);
        logger.debug("signUp: Получен UserRegistrationDTO: " + user);
        User newUser = userService.createNewUser(user);
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.CREATED)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/octet-stream")
                .addHeader(HttpHeader.CONNECTION, "close")
                .setBody("")
                .build();
    }

}
