package shop.shportfolio.user.application.command.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadUserImageCommand {
    private UUID userId;
    private String originalFileName;
    private byte[] fileContent;

}
