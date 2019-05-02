package com.github.thoainguyen.grpc.greeting.client;


import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        System.out.println("Creating Stub");

        // old and Dummy
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
        // DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);

        GreetServiceGrpc.GreetServiceBlockingStub syncGreetClient = GreetServiceGrpc.newBlockingStub(channel);


        /* Unary
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Thoai")
                .setLastName("Nguyen")
                .build();
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse response = syncGreetClient.greet(greetRequest);

        System.out.println(response.getResult());
        */


        // Server Streamming

        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Thoai").setLastName("Nguyen"))
                .build();

        // we stream the response (in blocking maner)
        syncGreetClient.greetManyTimes(request)
                .forEachRemaining( response -> {
                    System.out.println(response.getResult());
                });

        // do something
        System.out.println("Shutdown channel");
        channel.shutdown();
    }
}
