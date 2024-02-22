package darede.larissagalao.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.slf4j.SLF4JSegmentListener;
import com.amazonaws.xray.strategy.jakarta.SegmentNamingStrategy;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import jakarta.servlet.Filter;
import java.net.URL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
  @Bean
  public Filter TracingFilter() {
    AWSXRayRecorderBuilder builder =
        AWSXRayRecorderBuilder.standard()
            .withSegmentListener(new SLF4JSegmentListener("CUSTOM-PREFIX"));

    URL ruleFile = WebConfig.class.getResource("/sampling-rules.json");
    builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));

    AWSXRay.setGlobalRecorder(builder.build());

    return new AWSXRayServletFilter(SegmentNamingStrategy.dynamic("Scorekeep"));
  }
}
