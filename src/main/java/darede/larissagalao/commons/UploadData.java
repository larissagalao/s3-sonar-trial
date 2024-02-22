package darede.larissagalao.commons;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadData {

  private Integer partNumber;
  private String uploadId;
  private Map<Integer, String> partETags;
  private Boolean start;
  private Boolean stop;
  private String fileName;
  private byte[] uploadData;
}
