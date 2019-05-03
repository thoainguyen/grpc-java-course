package com.github.thoainguyen.grpc.greeting.client;


import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    ManagedChannel channel;

    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

    private void run(){
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // doUnaryCall(channel);
        // doServerStreamingCall(channel);

        doClientStreamingCall(channel);

        System.out.println("Shutdown channel");
        channel.shutdown();
    }

    private void doClientStreamingCall(ManagedChannel channel) {
        // created an asynchronous client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

         CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // we get a response from the server
                System.out.println("Received a response from the server");
                System.out.println(value.getResult());
                // onNext() will be call only once
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from a server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                // onCompleted will be called right after onNext()
                System.out.println("Server has completed sending us something");
                 latch.countDown();
            }
        });

        // streaming message #1
        System.out.println("sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Thoai")
                        .build())
                .build());
        // streaming message #2
        System.out.println("sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Nguyen")
                        .build())
                .build());
        // streaming message #3
        System.out.println("sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Huynh")
                        .build())
                .build());

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void doUnaryCall(ManagedChannel channel) {
        // created a greet service client (blocking -synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Unary
        // created a protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Thoai")
                .setLastName("Nguyen")
                .build();
        // do the same for GreetRequest
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse response = greetClient.greet(greetRequest);
        System.out.println(response.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel) {
        // Server Streamming
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);


        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Thoai").setLastName("Nguyen"))
                .build();

        // we stream the response (in blocking maner)
        greetClient.greetManyTimes(request)
                .forEachRemaining( response -> {
                    System.out.println(response.getResult());
                });
    }

}
