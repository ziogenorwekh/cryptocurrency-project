package shop.shportfolio.user.application.exception.s3;

public class CustomAmazonS3Exception extends RuntimeException {
    public CustomAmazonS3Exception(String message) {
        super(message);
    }
    public CustomAmazonS3Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
