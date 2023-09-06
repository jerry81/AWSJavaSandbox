import com.amazonaws.services.iot.AWSIot;
// import com.amazonaws.regions.Region;
import com.amazonaws.services.iot.AWSIotClientBuilder;
// import com.amazonaws.services.iot.model.RegisterCertificateRequest;
import com.amazonaws.services.iot.model.RegisterCertificateResult;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
// import com.amazonaws.services.iot.model.CreateCertificateFromCsrRequest;
// import com.amazonaws.services.iot.model.CreateCertificateFromCsrResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateCertificateFromCsrRequest;
import software.amazon.awssdk.services.iot.model.CreateCertificateFromCsrResponse;


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

    private static void createCert2() {
      IotClient ic = IotClient.builder().build();
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

    private static void createPolicy() {
      final String policyName = "test_sensor_TEST-SERIAL";
      final String policyTemplate = "fwr30.json";

    }

    public static void main(String[] args) {
        // createCert();
       createCert2();
       createPolicy();
    }
}
