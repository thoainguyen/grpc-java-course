package com.github.thoainguyen.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50053)
                .addService(new BlogServerImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread ( () -> {
            server.shutdown();
            System.out.println("Succesfully Shutdown Server");
        }));

        server.awaitTermination();
    }
}
