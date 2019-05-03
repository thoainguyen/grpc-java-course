package com.github.thoainguyen.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    ManagedChannel channel;

    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.run();
    }

    private void run(){
         channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

         // doUnaryCall(channel);
         // doServerStreamingCall(channel);
         doClientStreamingCall(channel);
         channel.shutdown();

    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub ayncClient =
                CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = ayncClient.computeAverage(
                new StreamObserver<ComputeAverageResponse>() {

            @Override
            public void onNext(ComputeAverageResponse value) {
                System.out.println("Received a response from the server");
                System.out.println(value.getAverage());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us data");
                latch.countDown();
            }
        });

        requestObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(1).build());

        requestObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(2).build());

        requestObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(3).build());

        requestObserver.onNext(ComputeAverageRequest.newBuilder()
                .setNumber(4).build());

        // we expect the average to be 10/4 = 2.5
        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);

        Integer number = 567890;

        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number).build())
                .forEachRemaining(response -> {
                    System.out.println(response.getPrimeFactor());
                });
    }

    private void doUnaryCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
                CalculatorServiceGrpc.newBlockingStub(channel);

        SumRequest request = SumRequest.newBuilder()
                .setFirstNumber(10)
                .setSecondNumber(20)
                .build();

        SumResponse response = stub.sum(request);

        System.out.println("Result : " + response.getSumResult());
    }
}

