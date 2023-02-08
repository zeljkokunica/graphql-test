package com.viabill.graphqltest.criipto;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.viabill.criipto.signature.generated.AddSignatoryInput;
import com.viabill.criipto.signature.generated.AddSignatoryOutput;
import com.viabill.criipto.signature.generated.CloseSignatureOrderInput;
import com.viabill.criipto.signature.generated.CloseSignatureOrderOutput;
import com.viabill.criipto.signature.generated.CreateSignatureOrderInput;
import com.viabill.criipto.signature.generated.CreateSignatureOrderOutput;
import com.viabill.criipto.signature.generated.DocumentInput;
import com.viabill.criipto.signature.generated.DocumentStorageMode;
import com.viabill.criipto.signature.generated.PadesDocumentInput;
import com.viabill.criipto.signature.generated.PdfSealPosition;
import com.viabill.criipto.signature.generated.SignatoryDocumentInput;
import com.viabill.criipto.signature.generated.SignatoryEvidenceValidationInput;
import com.viabill.criipto.signature.generated.SignatureOrder;
import com.viabill.graphqltest.criipto.repository.CriiptoSignaturesGraphQLRepository;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CriiptoClient {

    private final CriiptoSignaturesGraphQLRepository criiptoSignaturesGraphQLRepository;

    public CreateSignatureOrderOutput createSignatureOrder(String documentTitle, byte[] documentContent)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        final CreateSignatureOrderInput request = new CreateSignatureOrderInput();
        final DocumentInput productAgreement = new DocumentInput();
        final PadesDocumentInput pdf = new PadesDocumentInput();
        pdf.setTitle(documentTitle);
        pdf.setBlob(Base64.getEncoder().encodeToString(documentContent));
        pdf.setStorageMode(DocumentStorageMode.Temporary);
        productAgreement.setPdf(pdf);
        request.setDocuments(List.of(productAgreement));
        return criiptoSignaturesGraphQLRepository.createSignatureOrder(request);
    }

    public AddSignatoryOutput addSignatory(String signatureOrderId, String cprNumber)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        final AddSignatoryInput request = new AddSignatoryInput();
        request.setSignatureOrderId(signatureOrderId);
        final SignatoryEvidenceValidationInput validationInput = new SignatoryEvidenceValidationInput();
        validationInput.setKey("cprNumberIdentifier");
        validationInput.setValue(cprNumber);
        request.setEvidenceValidation(List.of(validationInput));
        SignatoryDocumentInput di = new SignatoryDocumentInput();
        di.setPdfSealPosition(PdfSealPosition.builder().withPage(1).withX(15.0).withY(15.0).build());
        request.setDocuments(List.of(di));
        return criiptoSignaturesGraphQLRepository.addSignatory(request);
    }

    public SignatureOrder getOrder(String signatureOrderId)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        return criiptoSignaturesGraphQLRepository.getSignatureOrder(signatureOrderId);
    }

    public SignatureOrder getOrderDocuments(String signatureOrderId)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        return criiptoSignaturesGraphQLRepository.getSignatureOrderDocuments(signatureOrderId);
    }

    public CloseSignatureOrderOutput closeSignatureOrder(String signatureOrderId)
            throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
        final CloseSignatureOrderInput request = new CloseSignatureOrderInput();
        request.setSignatureOrderId(signatureOrderId);
        return criiptoSignaturesGraphQLRepository.closeSignatureOrder(request);
    }
}
