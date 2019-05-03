package com.github.thoainguyen.grpc.blog.client;

import com.proto.blog.Blog;
import com.proto.blog.BlogServiceGrpc;
import com.proto.blog.CreateBlogRequest;
import com.proto.blog.CreateBlogResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    public static void main(String[] args) {
        BlogClient client = new BlogClient();
        client.run();
    }

    private void run() {
        ManagedChannel channel;
        channel = ManagedChannelBuilder.forAddress("localhost", 50053)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setAuthorId("thoainh")
                .setTitle("New Blog!")
                .setContent("Hello world this is my first blog")
                .build();

        CreateBlogResponse createResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog).build());

        System.out.println(createResponse);

        System.out.println("Received Create the blog");
        channel.shutdown();

    }
}
