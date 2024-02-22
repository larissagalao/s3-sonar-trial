package darede.larissagalao.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import darede.larissagalao.commons.UploadData;
import darede.larissagalao.exceptions.S3ExceptionHandler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@XRayEnabled
public class AmazonS3Service {

  @Autowired private AmazonS3 s3Client;

  @Value("${application.bucket.name}")
  private String bucketName;

  public UploadData uploadFile(UploadData uploadInfo) {
    log.info("X-Ray Segment Information: {}", AWSXRay.getCurrentSegment());
    log.info("Starting file upload.");

    byte[] fileBytes = uploadInfo.getUploadData();

    if (uploadInfo.getStart()) {

      String fileName = "backup" + LocalDateTime.now() + ".FBK";
      log.info("File name: " + fileName);
      InitiateMultipartUploadRequest initiateRequest =
          new InitiateMultipartUploadRequest(bucketName, fileName);
      InitiateMultipartUploadResult initResponse =
          s3Client.initiateMultipartUpload(initiateRequest);
      Map<Integer, String> partETags = new HashMap<>();
      uploadInfo.setPartETags(partETags);
      uploadInfo.setPartNumber(1);
      uploadInfo.setUploadId(initResponse.getUploadId());
      uploadInfo.setFileName(fileName);
      uploadInfo.setStart(Boolean.FALSE);
    }

    try {
      long partSize = 5 * 1024 * 1024;

      byte[] buffer = new byte[(int) partSize];
      ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
      long contentLength = fileBytes.length;

      while (contentLength > 0) {
        int bytesRead = inputStream.read(buffer, 0, (int) Math.min(partSize, contentLength));
        UploadPartRequest uploadRequest =
            new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(uploadInfo.getFileName())
                .withUploadId(uploadInfo.getUploadId())
                .withPartNumber(uploadInfo.getPartNumber())
                .withInputStream(new ByteArrayInputStream(buffer, 0, bytesRead))
                .withPartSize(bytesRead);

        UploadPartResult uploadPartResult = s3Client.uploadPart(uploadRequest);

        contentLength -= bytesRead;

        uploadInfo.setPartNumber(uploadInfo.getPartNumber() + 1);
        uploadInfo
            .getPartETags()
            .put(
                uploadPartResult.getPartETag().getPartNumber(),
                uploadPartResult.getPartETag().getETag());
      }

      if (uploadInfo.getStop()) {
        List<PartETag> list = new ArrayList<>();

        for (Integer s : uploadInfo.getPartETags().keySet()) {
          PartETag e = new PartETag(s, uploadInfo.getPartETags().get(s));
          list.add(e);
        }
        CompleteMultipartUploadRequest completeRequest =
            new CompleteMultipartUploadRequest(
                bucketName, uploadInfo.getFileName(), uploadInfo.getUploadId(), list);

        s3Client.completeMultipartUpload(completeRequest);

        uploadInfo.setFileName(uploadInfo.getFileName());
        log.info("Upload done.");

        return uploadInfo;
      }

      return uploadInfo;

    } catch (Exception e) {
      s3Client.abortMultipartUpload(
          new AbortMultipartUploadRequest(
              bucketName, uploadInfo.getFileName(), uploadInfo.getUploadId()));
      throw e;
    }
  }

  public ResponseEntity<byte[]> downloadFile(String fileName)
      throws S3ExceptionHandler, IOException {

    log.info("X-Ray Segment Information: {}", AWSXRay.getCurrentSegment());
    log.info("Starting file download.");

    S3Object s3Object = s3Client.getObject(bucketName, fileName);
    S3ObjectInputStream inputStream = s3Object.getObjectContent();

    try {

      byte[] content = IOUtils.toByteArray(inputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

      headers.setContentDispositionFormData("attachment", fileName);

      log.info("Download done.");

      return new ResponseEntity<>(content, headers, HttpStatus.OK);

    } catch (Exception e) {
      throw new S3ExceptionHandler("Error to download file.");
    } finally {
      if(s3Object != null){
        s3Object.close();
      }if(inputStream != null){
        inputStream.close();
      }
    }
  }

  public void validateBytes(byte[] fileObj, Integer fileLength) throws S3ExceptionHandler {
    Integer bytes = fileObj.length;
    if (fileLength.equals(bytes)) {
      log.info("byte[] validate");
    } else {
      throw new S3ExceptionHandler("error to validate byte[]. The size are not equals");
    }
  }
}
