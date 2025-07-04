package shop.shportfolio.user.infrastructure.s3.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.ports.output.s3.S3BucketAdapter;
import shop.shportfolio.user.application.exception.s3.CustomAmazonS3Exception;

import java.io.File;

@Slf4j
@Component
public class S3BucketAdapterImpl implements S3BucketAdapter {

    private final AmazonS3 amazonS3;

    private final String bucket;
    private final Environment env;

    @Autowired
    public S3BucketAdapterImpl(AmazonS3 amazonS3, Environment env) {
        this.amazonS3 = amazonS3;
        this.env = env;
        this.bucket = env.getProperty("cloud.aws.s3.bucket");
    }


    @Override
    public String uploadS3ProfileImage(File file) {
        validateFileType(file);
        upload(file);
        return amazonS3.getUrl(bucket, file.getName()).toString();
    }

    @Override
    public void deleteS3ProfileImage(String storageName) {
        try {
            if (!amazonS3.doesObjectExist(bucket, storageName)) {
                log.info("Object does not exist in bucket. Filename: {}", storageName);
                return;
            }

            // 객체 삭제
            amazonS3.deleteObject(bucket, storageName);
            log.info("Storage removal successful. Filename: {}", storageName);
        } catch (Exception e) {
            throw new CustomAmazonS3Exception("delete failed.", e);
        }
    }

    private void upload(File file) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, file.getName(), file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (Exception e) {
            log.error("upload failed: {}", e.getMessage());
            throw new CustomAmazonS3Exception("upload failed.");
        }
    }


    private void validateFileType(File file) {
        if (file == null) {
            throw new CustomAmazonS3Exception("File is null");
        }
        String fileName = file.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".webp"))) {
            throw new CustomAmazonS3Exception("File type not supported");
        }
    }
}
