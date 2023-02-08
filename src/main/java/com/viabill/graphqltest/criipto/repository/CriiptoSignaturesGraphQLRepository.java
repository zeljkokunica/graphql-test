package com.viabill.graphqltest.criipto.repository;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.viabill.criipto.signature.generated.AddSignatoryInput;
import com.viabill.criipto.signature.generated.AddSignatoryOutput;
import com.viabill.criipto.signature.generated.CloseSignatureOrderInput;
import com.viabill.criipto.signature.generated.CloseSignatureOrderOutput;
import com.viabill.criipto.signature.generated.CreateSignatureOrderInput;
import com.viabill.criipto.signature.generated.CreateSignatureOrderOutput;
import com.viabill.criipto.signature.generated.MutationExecutorCriiptoSignature;
import com.viabill.criipto.signature.generated.QueryExecutorCriiptoSignature;
import com.viabill.criipto.signature.generated.SignatureOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CriiptoSignaturesGraphQLRepository {

    private final MutationExecutorCriiptoSignature mutationExecutor;
    private final QueryExecutorCriiptoSignature queryExecutor;

    public CreateSignatureOrderOutput createSignatureOrder(CreateSignatureOrderInput request)
            throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        return mutationExecutor.getGraphQLRequest("""
            mutation CreateSignatureOrder($input: CreateSignatureOrderInput!) {
                createSignatureOrder(input: $input) {
                    signatureOrder {
                        id
                        documents {
                            id
                            title
                        }
                        signatories {
                            id
                            status
                        }
                    }
                }
              }
          """).execMutation("input", request).getCreateSignatureOrder();
    }

    public CloseSignatureOrderOutput closeSignatureOrder(CloseSignatureOrderInput request)
            throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        return mutationExecutor.closeSignatureOrder("""
                {
                  signatureOrder {
                    id
                    closedAt
                    status
                   } 
                }
                """, request);
    }

    public AddSignatoryOutput addSignatory(AddSignatoryInput request)
            throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        return mutationExecutor.getGraphQLRequest("""
                  mutation addSignatory(
                     $input: AddSignatoryInput!
                   ) {
                     addSignatory(input: $input) {
                       signatory {
                         id
                         href
                         token
                       }
                     }
                   }
                """).execMutation("input", request).getAddSignatory();
    }

    public SignatureOrder getSignatureOrder(String orderId)
            throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        return queryExecutor.signatureOrder("""
                {
                    id
                    createdAt
                    status
                    signatories {
                      id
                      status
                    }
                } 				
                """, orderId);
    }

    public SignatureOrder getSignatureOrderDocuments(String orderId)
            throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
        return queryExecutor.signatureOrder("""
                {
                    id
                    createdAt
                    status
                    documents {
                      id
                      title
                      blob
                      reference
                    }
                    signatories {
                      id
                      status
                      reference
                    }
                } 				
                """, orderId);
    }

}
