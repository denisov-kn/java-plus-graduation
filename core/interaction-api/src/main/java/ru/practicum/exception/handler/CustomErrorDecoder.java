package ru.practicum.exception.handler;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import ru.practicum.exception.types.InternalServerErrorException;
import ru.practicum.exception.types.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {

        String message = "Ошибка сервера";

        if(response.body() != null) {
            try {
                message = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (response.status() == 404)
            return new NotFoundException(methodKey + ": " +  message);

        if (response.status() == 500) {
            return new InternalServerErrorException(methodKey + ": " + message);
        }

        return defaultDecoder.decode(methodKey, response);
    }
}
