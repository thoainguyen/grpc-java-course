package com.github.thoainguyen.grpc.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {

    ManagedChannel channel;

    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.run();
    }

    private void run() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
                .build();

        // doUnaryCall(channel);
        // doServerStreamingCall(channel);
        // doClientStreamingCall(channel);
        doBiDiStreamingCall(channel);
        channel.shutdown();

    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient =
                CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<FindMaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("Got new maximum from Server " + value.getMaximum());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });


        Arrays.asList(3, 5, 17, 9 , 8 , 30, 12).forEach(
                number -> {
                    System.out.println("Sending number : " + number);
                    requestObserver.onNext(
                            FindMaximumRequest.newBuilder()
                                    .setNumber(number)
                                    .build()
                    );

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient =
                CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = asyncClient.computeAverage(
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

        // we send 1000 request to our server (client streaming)
        for (int i = 0; i < 1000; i++) {
            requestObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(i)
                    .build());
        }

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

