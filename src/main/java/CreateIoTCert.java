/*
old sdk
*/
// import com.amazonaws.services.iot.AWSIot;
// import com.amazonaws.regions.Region;
// import com.amazonaws.services.iot.AWSIotClientBuilder;
// import com.amazonaws.services.iot.model.RegisterCertificateRequest;
// import com.amazonaws.services.iot.model.RegisterCertificateResult;
// import com.amazonaws.regions.AwsRegionProvider;
// import com.amazonaws.regions.DefaultAwsRegionProviderChain;
// import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
// import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;


/* new sdk */
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateCertificateFromCsrRequest;
import software.amazon.awssdk.services.iot.model.CreateCertificateFromCsrResponse;
import software.amazon.awssdk.services.iot.model.GetPolicyRequest;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;
import software.amazon.awssdk.services.iot.model.CreatePolicyRequest;

public class CreateIoTCert {
    private static String readCSRFromFile(String filePath) {
        try {
            ClassLoader classLoader = CreateIoTCert.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(filePath);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            } else {
                System.err.println("CSR resource not found: " + filePath);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createCert2(IotClient ic) {
      CreateCertificateFromCsrRequest request = CreateCertificateFromCsrRequest.builder()
        .certificateSigningRequest(readCSRFromFile("csr.pem"))
        .build();
      CreateCertificateFromCsrResponse response = ic.createCertificateFromCsr(request);
      System.out.println("arn is " + response.certificateArn());
      System.out.println("id is " + response.certificateId());
      System.out.println("pem is " + response.certificatePem());

    }


    // private static void createCert() {
    //     AWSIot awsIotClient = AWSIotClientBuilder.standard().build();

    //     // Read your CSR from a file or another source
    //     String csrContents = readCSRFromFile("csr.pem");
    //     AwsRegionProvider regionProvider = new DefaultAwsRegionProviderChain();
    //     String region = regionProvider.getRegion();

    //     System.out.println("Configured Region: " + region);
    //     // Create a request to register the certificate

    //     CreateCertificateFromCsrRequest request = new CreateCertificateFromCsrRequest();
    //     request.setCertificateSigningRequest(csrContents);


    //     // Register the certificate
    //      CreateCertificateFromCsrResult result = awsIotClient.createCertificateFromCsr(request);

    //     // Print certificate ARN and ID
    //      System.out.println("Certificate ARN: " + result.getCertificateArn());
    //      System.out.println("Certificate ID: " + result.getCertificateId());
    // }

    private static void createPolicy(IotClient ic) {
      final String policyName = "test_sensor_TEST-SERIAL";
      final String policyTemplate = "fwr30.json";
      final GetPolicyRequest request = GetPolicyRequest.builder().policyName(policyName).build();
      try {
        ic.getPolicy(request);
        System.error.println("policy already exists");
      } catch (final ResourceNotFoundException e) {
        System.out.println("policy does not exist");

        try (final InputStream input = CreateIoTCert.class.getClassLoader().getResourceAsStream(policyTemplate)) {
          String template = IOUtils.toString(input, StandardCharsets.UTF_8);

          final Function<String, String> placeholder = key -> "__" + key + "__";

          template = RegExUtils.replaceAll(template, placeholder.apply("serial_number"), "TEST-SERIAL");
          template = RegExUtils.replaceAll(template, placeholder.apply("IOT_REGION"), "cn-north-1");
          template = RegExUtils.replaceAll(template, placeholder.apply("AWS_ACCOUNT"), "947302404220");
          final CreatePolicyRequest policyRequest = CreatePolicyRequest.builder().//
                    policyName(policyName).//
                    policyDocument(template).//
                    build();
            ic.createPolicy(policyRequest);
          System.out.println("Created policy " + policyName);
        } catch (IOException e) {
            e.printStackTrace();
        }
      }

    }

    public static void main(String[] args) {
        // createCert();
       IotClient ic = IotClient.builder().build();
       createCert2(ic);
       createPolicy(ic);
    }
}
