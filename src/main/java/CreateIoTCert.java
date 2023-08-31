import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.regions.Region;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.RegisterCertificateRequest;
import com.amazonaws.services.iot.model.RegisterCertificateResult;
import com.amazonaws.regions.AwsRegionProvider;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
    public static void main(String[] args) {
        // Set up AWS IoT client
        AWSIot awsIotClient = AWSIotClientBuilder.standard().build();

        // Read your CSR from a file or another source
        String csrContents = readCSRFromFile("csr_file.csr");

        AwsRegionProvider regionProvider = new DefaultAwsRegionProviderChain();
        String region = regionProvider.getRegion();

        System.out.println("Configured Region: " + region);
        // Create a request to register the certificate
        RegisterCertificateRequest request = new RegisterCertificateRequest();
        request.setSetAsActive(true); // Set to true to activate the certificate
        request.setCertificatePem(csrContents);

        // Register the certificate
        RegisterCertificateResult result = awsIotClient.registerCertificate(request);

        // Print certificate ARN and ID
        System.out.println("Certificate ARN: " + result.getCertificateArn());
        System.out.println("Certificate ID: " + result.getCertificateId());
    }
}
