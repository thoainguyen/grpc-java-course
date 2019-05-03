package com.github.thoainguyen.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {

        // extract the fields we need
        Greeting greeting = request.getGreeting();
        String firstname = greeting.getFirstName();
        String lastname = greeting.getLastName();

        String result = "Hello " + firstname + " " + lastname;

        // create the response
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();

    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver){
        String firstname = request.getGreeting().getFirstName();

        try {
            for(int i = 0; i < 10; i ++){
                String result = "Hello " + firstname + ", response number : " + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(response);
                Thread.sleep(1000L);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {

        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message
                result += "Hello " + value.getGreeting().getFirstName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client is Done
                responseObserver.onNext(
                        LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build()
                );
                responseObserver.onCompleted();
                // when we want to return a response (responseObserver)
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryOneRequest> greetEveryOne(StreamObserver<GreetEveryOneResponse> responseObserver) {
        StreamObserver<GreetEveryOneRequest> requestObserver = new StreamObserver<GreetEveryOneRequest>() {
            @Override
            public void onNext(GreetEveryOneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName();
                GreetEveryOneResponse greetEveryOneResponse = GreetEveryOneResponse.newBuilder()
                        .setResult(result).build();
                responseObserver.onNext(greetEveryOneResponse);
            }

            @Override
            public void onError(Throwable t) {
                // do nothing
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try{
            for(int i = 0; i < 3; i ++){
                if (!current.isCancelled()) {
                    System.out.println("Sleep for 100ms");
                    Thread.sleep(100);
                } else {
                    return;
                }
            }

            responseObserver.onNext(
                    GreetWithDeadlineResponse.newBuilder()
                            .setResult("Hello " + request.getGreeting().getFirstName())
                            .build()
            );

            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
