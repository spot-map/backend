package com.ridemap.backend.common.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class FsConfig {
    @Bean 
    public MinioClient minioClient(
            @Value("${rustfs.endpoint}") String endpoint,
            @Value("${rustfs.access-key}") String accessKey,
            @Value("${rustfs.secret-key}") String secretKey
    ){
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    @Bean 
    public FsBucketInitializer fsBucketInitializer(MinioClient client, @Value("${rustfs.bucket}") String bucket){
        return new FsBucketInitializer(client, bucket);
    }


    public static class FsBucketInitializer{
        public FsBucketInitializer(MinioClient client, String bucket){
            try{
                boolean exist = client.bucketExists(
                    BucketExistsArgs.builder()
                    .bucket(bucket)
                    .build()   
                );

                if(!exist){
                    client.makeBucket(
                        MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build()  
                    );
                }

            }catch(Exception ex){
                throw new IllegalStateException(
                    "Ошибка проверки или создания" + bucket,
                    ex
                );
            }
        }
    }
}
