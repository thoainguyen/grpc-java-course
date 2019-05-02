package com.github.thoainguyen.grpc.calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();

        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request,
         StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer diviser = 2;


        while (number >  1){
            if(number % diviser == 0){
                number = number/ diviser;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                    .setPrimeFactor(number)
                    .build()
                );
            }
            else{
                diviser = diviser + 1;
            }
        }
        responseObserver.onCompleted();
    }
}
