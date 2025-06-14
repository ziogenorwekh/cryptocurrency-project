package shop.shportfolio.user.application.generator;

import org.springframework.stereotype.Component;
import shop.shportfolio.user.application.exception.UserApplicationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class DefualtFileGenerator implements FileGenerator {
    @Override
    public File convertByteArrayToFile(UUID userId, byte[] bytes, String fileName) {
        try {
            File tempFile = File.createTempFile(userId.toString(), "_" + fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(bytes);
            }
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
