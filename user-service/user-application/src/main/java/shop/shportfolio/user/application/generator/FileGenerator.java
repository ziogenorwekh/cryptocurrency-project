package shop.shportfolio.user.application.generator;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public interface FileGenerator {

    File convertByteArrayToFile(UUID userId, byte[] bytes, String fileName);
}
