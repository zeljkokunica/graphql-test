package com.viabill.graphqltest.criipto;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.viabill.criipto.signature.generated.AddSignatoryOutput;
import com.viabill.criipto.signature.generated.CreateSignatureOrderOutput;
import com.viabill.criipto.signature.generated.SignatoryStatus;
import com.viabill.criipto.signature.generated.SignatureOrder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CriiptoTestOperation {

    private final CriiptoClient client;
    private final String signedFileOutput;

    public CriiptoTestOperation(CriiptoClient client, @Value("${criipto.signed-file-output}") String signedFileOutput) {
        this.client = client;
        this.signedFileOutput = signedFileOutput;
    }

    public void submitDocumentForSignature() {
        try {
            final CreateSignatureOrderOutput order = client.createSignatureOrder("Test", readDocument());
            final String orderId = order.getSignatureOrder().getId();
            System.out.println(order.getSignatureOrder().getDocuments().get(0).getBlob());
            final AddSignatoryOutput signatory = client.addSignatory(orderId, "0402773903");
            log.info("Open signing URL: " + signatory.getSignatory().getHref());
            SignatureOrder readOrder = client.getOrder(orderId);
            while (readOrder.getSignatories().get(0).getStatus() == SignatoryStatus.OPEN) {
                try {
                    log.info("waiting for signature...");
                    Thread.sleep(3000);
                    readOrder = client.getOrder(orderId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Signing completed");
            final SignatureOrder finalOrder = client.getOrderDocuments(orderId);
            final SignatoryStatus signatoryStatus = finalOrder.getSignatories().get(0).getStatus();
            if (signatoryStatus == SignatoryStatus.SIGNED) {
                String blobContent = finalOrder.getDocuments().get(0).getBlob();
                byte[] decodedBytes = Base64.getDecoder().decode(blobContent);
                saveOutput(decodedBytes);
                client.closeSignatureOrder(orderId);
            } else {
                log.warn("Signature failed with status {}", signatoryStatus);
            }
        } catch (GraphQLRequestExecutionException e) {
            throw new RuntimeException("Failed to create signature", e);
        } catch (GraphQLRequestPreparationException e) {
            throw new RuntimeException(e);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to save signed PDF", e);
        }
    }

    private byte[] readDocument() throws IOException, URISyntaxException {
        return Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("dummy.pdf").toURI()));
    }

    private void saveOutput(byte[] decodedBytes) throws IOException {
        Files.write(Paths.get(signedFileOutput), decodedBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }
}
