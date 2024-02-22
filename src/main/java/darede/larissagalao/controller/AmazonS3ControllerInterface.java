package darede.larissagalao.controller;

import darede.larissagalao.commons.UploadData;
import darede.larissagalao.exceptions.S3ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/s3")
public interface AmazonS3ControllerInterface {

  @PostMapping(value = "/upload")
  ResponseEntity<UploadData> uploadFile(@RequestBody UploadData uploadData)
      throws S3ExceptionHandler;

  @GetMapping("/download/{backupName}")
  ResponseEntity<byte[]> downloadFile(@PathVariable String backupName) throws S3ExceptionHandler;

  @GetMapping("/health")
  ResponseEntity<?> testMessage();
}
