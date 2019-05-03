# grpc-java-course
Learn gRPC by follow [here](https://learning.oreilly.com/videos/grpc-java-master/9781838558048/)

# gRPC feature

* Compare to Rest/JSON base API
  * Cros
    * Use binary data format -> easy serialize and deserialize by machine
    * Smaller -> faster
    * ...
  * Cons
    * Few tools support for testing

* Backward of HTTP/1
  * Create new connection for new request
  * Modal request/response -> server can response only they received a request from client before
  * Data isn't compressed

* gRPC using HTTP/2 as transport layer
  * Mutiflexing : mutiple message in a connection
  * Server push : Server can response stream of message with a request from client before
  * Bidirectional : Server and client can asynchronous communicate each other
  * Header compression: header can be compressed so that that's size is smaller -> faster
  * Secure : Base on SSL/TSL protocol, gRPC is secure by default 

# gRPC basic

* Unary
* Server Streaming
* Client Streaming
* Bidirectional streaming

# gRPC advanced

* Handling Errors, for more infomation read here https://grpc.io/docs/guides/error/
* Deadline call, https://grpc.io/blog/deadlines/
* SSL Security
  * Note : on production : use SSL instead .usePlaintext() to ensure security
  * on developement: use .usePlaintext() for simple and fast
* gRPC Language Interoperability : you can create client and server with isn't same languague
* gRPC Reflection and Evans CLI : useful tools support gRPC client command line is [evans](https://github.com/ktr0731/evans) 
    * Install evans for gRPC client https://github.com/ktr0731/evans
    * On linux download file evans-package.tar.gz
    
    ```$xslt
    
    $ tar xvzf evans-package.tar.gz // untar
    $ sudo mv evans /usr/local/bin  // for access everywhere from terminal
    $ evans --cli -r -p 50052       // open grpc-cli
    ```

