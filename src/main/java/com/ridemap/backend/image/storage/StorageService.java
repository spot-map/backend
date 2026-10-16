package com.ridemap.backend.image.storage;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

@Service 
public class StorageService {
    private final MinioClient client;
    private  final String bucket;

    public StorageService(MinioClient client, @Value("${rustfs.bucket}") String bucket){
        this.client = client;
        this.bucket = bucket;
    }

    public void upload(
        String objectKey,
        InputStream inputStream,
        long size,
        String contentType
    ){
        try{
           client.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectKey)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build()  
           );
        }catch(Exception ex){
            throw new StorageException("Не удалось загрузить файл в RustFS", ex);
        }
    }


    public InputStream download(String objectKey){
      try{
        return client.getObject(
            GetObjectArgs.builder()
            .bucket(bucket)
            .object(objectKey)
            .build()
        );
      }catch(Exception ex){
        throw new StorageException("Не удалось получить файл в RustFS", ex);
      }


    }

    public void delete(String objectKey){
        try{
           client.removeObject(
            RemoveObjectArgs.builder()
            .bucket(bucket)
            .object(objectKey)
            .build()
           );
        }catch(Exception ex){
        throw new StorageException("Не удалось удалить файл в RustFS", ex);
      }
    }
}
