package shop.shportfolio.user.application.ports.output.s3;

import java.io.File;

public interface S3BucketAdapter {
    void uploadS3ProfileImage(File file);

    void deleteS3ProfileImage(String storageName);
}
