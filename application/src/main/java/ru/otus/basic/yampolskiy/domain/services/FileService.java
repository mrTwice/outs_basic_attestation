package ru.otus.basic.yampolskiy.domain.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.MultipartData;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileService {
    private static FileService fileService;
    private final Logger logger = LogManager.getLogger(FileService.class);

    private FileService() {
    }

    public static FileService getFileService() {
        if (fileService == null) {
            fileService = new FileService();
        }
        return fileService;
    }

    public File saveFile(MultipartData multipartData) throws IOException {
        logger.debug("Начало сохранения файла...");
        String boundary = multipartData.getBoundary();
        InputStream inputStream = multipartData.getInputStream();
        String s = new String(inputStream.readAllBytes());

        // Создание директории, если она не существует
        File uploadDir = new File("./uploads");
        if (!uploadDir.exists()) {
            logger.debug("Директория ./uploads не существует, создаем...");
            if (!uploadDir.mkdirs()) {
                logger.error("Не удалось создать директорию для загрузки файлов: " + uploadDir.getAbsolutePath());
                throw new IOException("Не удалось создать директорию для загрузки файлов: " + uploadDir.getAbsolutePath());
            }
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        String line;
        String fileName = null;
        File file = null;
        OutputStream fileOutputStream = null;

        return file;
    }

}


