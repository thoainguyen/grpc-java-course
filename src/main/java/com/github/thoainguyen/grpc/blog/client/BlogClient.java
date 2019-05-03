package com.github.thoainguyen.grpc.blog.client;

import com.proto.blog.*;
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

        System.out.println("Received Create the blog");
        CreateBlogResponse createResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog).build());

        System.out.println(createResponse);

        String blogId = createResponse.getBlog().getId();
        System.out.println("Reading blog....");

        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
        .setBlogId(blogId)
        .build());

        System.out.println(readBlogResponse);

        System.out.println("Reading blog with non exisiting id...");
        ReadBlogResponse readBlogResponseNotFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setBlogId("fake").build());
        System.out.println(readBlogResponseNotFound);
        channel.shutdown();

    }
}
