package darede.larissagalao.controller;

import darede.larissagalao.commons.UploadData;
import darede.larissagalao.exceptions.S3ExceptionHandler;
import darede.larissagalao.service.AmazonS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
public class AmazonS3Controller implements AmazonS3ControllerInterface {

  @Autowired private AmazonS3Service amazonS3Service;

  @Override
  public ResponseEntity<UploadData> uploadFile(UploadData uploadData) throws S3ExceptionHandler {
    try {
      return ResponseEntity.status(HttpStatus.OK).body(amazonS3Service.uploadFile(uploadData));
    } catch (Exception e) {
      throw new S3ExceptionHandler("Error to upload file. " + e);
    }
  }

  @Override
  public ResponseEntity<byte[]> downloadFile(@PathVariable String backupName)
      throws S3ExceptionHandler {
    try {
      return amazonS3Service.downloadFile(backupName);
    } catch (Exception e) {
      throw new S3ExceptionHandler("Error to download file. " + e);
    }
  }

  @Override
  public ResponseEntity<?> testMessage() {
    return ResponseEntity.ok().build();
  }
}
