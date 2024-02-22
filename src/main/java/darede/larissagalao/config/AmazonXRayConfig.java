package darede.larissagalao.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.xray.AWSXRayAsync;
import com.amazonaws.services.xray.AWSXRayAsyncClient;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonXRayConfig {

  @Bean
  public AWSXRayAsync xRayAsyncClient() {
    return AWSXRayAsyncClient.asyncBuilder()
        .withRegion(Regions.US_EAST_1)
        .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
        .build();
  }
}
