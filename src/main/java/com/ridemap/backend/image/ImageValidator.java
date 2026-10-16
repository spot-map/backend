package com.ridemap.backend.image;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component 
public class ImageValidator {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Map<String, byte[]> SIGNATURES = Map.of(
        "image/jpeg", new byte[]{
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF
        },
        "image/png", new byte[]{
            (byte) 0x89, 0x50,0x4E, 0x47,
            0x0D, 0x0A, 0x1A, 0x0A
        },
        "image/webp", new byte[]{
            0x52, 0x49, 0x46, 0x46
        }
    );

    public String validate(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Файл не передан или пуст"
            );
        }

        if(file.getSize() > MAX_FILE_SIZE){
            throw new ResponseStatusException(
               HttpStatus.PAYLOAD_TOO_LARGE,
               "Максимальный размер файла - 10 МБ"
            );
        }

        String contentType = file.getContentType();

        byte[] signature = SIGNATURES.get(contentType);

        if(signature == null){
            throw new ResponseStatusException(
               HttpStatus.UNSUPPORTED_MEDIA_TYPE,
               "Разрешены только JPEG, PNG и WebP"
            );
        }

        try{
            byte[] header = file.getInputStream().readNBytes(12);

            if(!hasSignature(header, signature)){
                throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Содержимое файла не соответствует формату изоюражения"
                );
            }

            if("image/webp".equals(contentType) && !isWebp(header)){
                throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Некорректный файл WEBP"
                );
            }

        }catch(IOException ex){
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Не удалось прочитать файл ",
                ex
            );
        }

        return  contentType;
    }

    private boolean hasSignature(byte[] header, byte[] signature){
        if(header.length < signature.length){
            return false;
        }

        for(int i = 0; i < signature.length; i++){
            if(header[i] != signature[i]){
                return false;
            }
        }

        return  true;
    }

    private boolean isWebp(byte[] header){
        return  header.length >= 12
            && header[0] == 'R'
            && header[1] == 'I'
            && header[2] == 'F'
            && header[3] == 'F'
            && header[8] == 'W'
            && header[9] == 'E'
            && header[10] == 'B'
            && header[11] == 'P';
    }
}
