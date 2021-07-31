package org.university.service.validator;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.university.exceptions.InvalidPhotoException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

@Component
@Slf4j
public class PhotoValidator implements Validator<MultipartFile> {

    @Override
    public void validate(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String mimeType = Magic.getMagicMatch(multipartFile.getBytes(), false).getMimeType();
            if (mimeType.startsWith("image/")) {
                BufferedImage image = ImageIO.read(inputStream);
                if (image.getHeight() > 66 || image.getWidth() > 66) {
                    log.error("Input file has invalid size!");
                    throw new InvalidPhotoException("photosize");
                }
            } else {
                log.error("Input file has invalid extension, it's not photo!");
                throw new InvalidPhotoException("photoextension");
            }
        } catch (IOException | MagicParseException | MagicMatchNotFoundException | MagicException e) {
            log.error("Inputing fail failed!");
        } 
    }
}
