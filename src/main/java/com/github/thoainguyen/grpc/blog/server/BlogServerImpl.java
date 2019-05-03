package com.github.thoainguyen.grpc.blog.server;


import com.mongodb.client.*;

import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;


public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {
    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database  = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        Blog blog = request.getBlog();

        System.out.println("Received Create Blog request");
        Document doc = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        // we insert (create) the document in mongoDB
        collection.insertOne(doc);
        // we retrieve the mogodb generated ID
        String id = doc.getObjectId("_id").toString();
        System.out.println("Inserting blog... " + id);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

        String blogId = request.getBlogId();

        System.out.println("Search for a blog");

        Document result = collection.find(eq("_id", new ObjectId(blogId)))
            .first();

        if (result == null){
            System.out.println("Blog not found");

            // we don't have a match
            responseObserver.onError(
                    Status.NOT_FOUND
                    .withDescription("The blog with the corresponding id was not found")
                    .asRuntimeException()
            );
        }
        else {
            System.out.println("Blog is found");

            Blog blog = Blog.newBuilder().setAuthorId(result.getString("author_id"))
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .setId(blogId)
                    .build();
            responseObserver.onNext(ReadBlogResponse.newBuilder()
                    .setBlog(blog).build());
            responseObserver.onCompleted();
        }


    }

}
