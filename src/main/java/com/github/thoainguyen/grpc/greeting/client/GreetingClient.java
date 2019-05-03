package com.github.thoainguyen.grpc.greeting.client;


import com.proto.greet.*;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {


    public static void main(String[] args) throws SSLException {
        System.out.println("Hello I'm a gRPC client");
        GreetingClient greetingClient = new GreetingClient();
        greetingClient.run();
    }

    private void run() throws SSLException {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // With server authentication SSL/TLS; custion CA root certificates; not on android
        ManagedChannel secureChannel = NettyChannelBuilder.forAddress("localhost", 50051)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
                .build();

        doUnaryCall(secureChannel);
        // doServerStreamingCall(channel);
        // doClientStreamingCall(channel);
        // doBiDiStreamingCall(channel);
        // doUnaryCallWithDeadline(channel);
        System.out.println("Shutdown channel");
        channel.shutdown();
    }

    private void doUnaryCallWithDeadline(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(channel);
        // first call (3000ms) deadline
        try{
            System.out.println("Sending a request with a deadline of 3000ms");
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(3000, TimeUnit.MILLISECONDS))
            .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                            .setFirstName("Thoai").build())
                    .build());
            System.out.println("Result : " + response.getResult());
        } catch (StatusRuntimeException e){
            if(e.getStatus() == Status.DEADLINE_EXCEEDED){
                System.out.println("Deadline has been exceed, we don't want the response");
            }
            else {
                e.printStackTrace();
            }
        }

        // second call (100ms) deadline
        try{
            System.out.println("Sending a request with a deadline of 100ms");
            GreetWithDeadlineResponse response = blockingStub.withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetWithDeadlineRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName("Thoai").build())
                            .build());
            System.out.println("Result : " + response.getResult());

        } catch (StatusRuntimeException e){
            if(e.getStatus() == Status.DEADLINE_EXCEEDED){
                System.out.println("Deadline has been exceed, we don't want the response");
            }
            else {
                e.printStackTrace();
            }
        }


    }

    private void doBiDiStreamingCall(ManagedChannel channel) {
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryOneRequest> requestObserver = asyncClient.greetEveryOne(
                new StreamObserver<GreetEveryOneResponse>() {
                    @Override
                    public void onNext(GreetEveryOneResponse value) {
                        System.out.println("Response from server: " + value.getResult());
                    }

                    @Override
                    public void onError(Throwable t) {
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Server is done sending data");
                        latch.countDown();
                    }
                }
        );

        Arrays.asList("John", "Marc", "Stephane").forEach(
                name -> {
                    System.out.println("Sending: " + name);
                    requestObserver.onNext(GreetEveryOneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name).build())
                            .build());
                    try { // we try to sleep for demo asynchronous request/response
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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


}
