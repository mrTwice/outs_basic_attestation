package ru.otus.basic.yampolskiy.domain.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.MultipartData;
import ru.otus.basic.yampolskiy.domain.services.FileService;
import ru.otus.basic.yampolskiy.servlets.annotations.PostRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.UploadedFile;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

import java.io.File;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Future;

@WebServlet("/files")
public class FileController extends HttpServlet {
    private final Logger logger = LogManager.getLogger(this);
    private final FileService fileService = FileService.getFileService();

    @PostRoute("/upload")
    public HttpServletResponse uploadUserFile(HttpServletRequest request, @UploadedFile MultipartData multipartData) throws Exception {

        File savedFile = fileService.saveFile(multipartData);

        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "UserServer/0.1")
                .setContentType("application/json")
                .setBody("{\"message\":\"File uploaded successfully\"}")
                .build();
    }
}
