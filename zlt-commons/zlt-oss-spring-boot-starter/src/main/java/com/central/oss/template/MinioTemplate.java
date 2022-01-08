package com.central.oss.template;

import com.central.oss.model.ObjectInfo;
import com.central.oss.properties.FileServerProperties;
import io.minio.*;
import io.minio.errors.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * minio 配置
 */
@ConditionalOnClass(MinioClient.class)
@ConditionalOnProperty(prefix = FileServerProperties.PREFIX, name = "type", havingValue = FileServerProperties.TYPE_MINIO)
public class MinioTemplate implements InitializingBean {
    private static final String DEF_CONTEXT_TYPE = "application/octet-stream";
    private static final String PATH_SPLIT = "/";

    @Autowired
    private FileServerProperties fileProperties;

    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() {
        this.minioClient = MinioClient.builder()
                .endpoint(fileProperties.getMinio().getEndpoint())
                .credentials(fileProperties.getMinio().getAccessKey(), fileProperties.getMinio().getAccessKeySecret())
                .build();
    }

    @SneakyThrows
    public ObjectInfo upload(String fileName, InputStream is) {
        return upload(fileProperties.getMinio().getBucketName(), fileName, is, is.available(), DEF_CONTEXT_TYPE);
    }

    @SneakyThrows
    public ObjectInfo upload(MultipartFile file) {
        return upload(fileProperties.getMinio().getBucketName(), file.getOriginalFilename(), file.getInputStream()
                , ((Long)file.getSize()).intValue(), file.getContentType());
    }

    @SneakyThrows
    public ObjectInfo upload(String bucketName, String fileName, InputStream is) {
        return upload(bucketName, fileName, is, is.available(), DEF_CONTEXT_TYPE);
    }

    /**
     * 上传对象
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param is 对象流
     * @param size 大小
     * @param contentType 类型
     */
    private ObjectInfo upload(String bucketName, String objectName, InputStream is, int size, String contentType) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectName)
                        .stream(is, size, -1)
                        .contentType(contentType)
                        .build());
        ObjectInfo obj = new ObjectInfo();
        obj.setObjectPath(bucketName + PATH_SPLIT + objectName);
        obj.setObjectUrl(fileProperties.getMinio().getEndpoint() + PATH_SPLIT + obj.getObjectPath());
        return obj;
    }

    public void delete(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        delete(fileProperties.getMinio().getBucketName(), objectName);
    }

    public void delete(String bucketName, String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.deleteObjectTags(DeleteObjectTagsArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 获取预览地址
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param expires 有效时间(分钟)，最大7天有效
     * @return
     */
    public String getViewUrl(String bucketName, String objectName, int expires) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, expires);
        String url =  minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(objectName).expiry(expires).build());
        return url;
    }

    public void out(String objectName, OutputStream os) {
        out(fileProperties.getMinio().getBucketName(), objectName, os);
    }

    /**
     * 输出对象
     * @param bucketName bucket名称
     * @param objectName 对象名
     * @param os 输出流
     */
    @SneakyThrows
    public void out(String bucketName, String objectName, OutputStream os) {
//        GetObjectResponse getObjectResponse = minioClient.getObject(
//                GetObjectArgs.builder().bucket(bucketName).object(objectName).build()
//        );

//        S3Object s3Object = minioClient.getObject(bucketName, objectName);
//        try (
//                S3ObjectInputStream s3is = s3Object.getObjectContent();
//        ) {
//            IOUtils.copy(s3is, os);
//        }
    }
}
