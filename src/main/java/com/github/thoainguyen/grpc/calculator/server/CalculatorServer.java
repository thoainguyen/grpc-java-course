package com.github.thoainguyen.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50051)
                .addService(new CalculatorServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook( new Thread ( () -> {
            System.out.println("Received Shutdown Server");
            server.shutdown();
            System.out.println("Succesfully Shutdown Server");
        }));

        server.awaitTermination();
    }
}
