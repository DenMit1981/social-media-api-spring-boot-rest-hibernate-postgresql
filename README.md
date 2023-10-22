1.Name of project: social-media-api-spring-boot-rest-hibernate-postgresql

2.Launch of project: run SocialMediaApplication.class

3.Port of the project: http://localhost:8081

4.Start page: http://localhost:8081

5.Swagger documentation: http://localhost:8081/v2/api-docs

6.Configuration: resources/application.properties

7.Database PostgreSQL connection:

Name: dbase@localhost
User: denmit
Password: 1981
Port: 5432

8.Rest controllers:

UserController:
registerUser(POST): http://localhost:8081 + body;
authenticationUser(POST): http://localhost:8081/auth + body
getAllPostsByUserId(GET): http://localhost:8081/users/{userId}/posts
getActivityFeedForUserByUserId(GET): http://localhost:8081/users/{userId}/activity-feed

PostController:
save(POST): http://localhost:8081/posts + body;
getAll(GET): http://localhost:8081/posts
getAllForCurrentUser(GET): http://localhost:8081/posts/my-posts
getActivityFeedForCurrentUser(GET): http://localhost:8081/posts/my-activity-feed;
getById(GET): http://localhost:8081/posts/{id};
update(PUT): http://localhost:8081/posts/{id} + body;
delete(DELETE): http://localhost:8081/posts/{id};

ImageController:
uploadFile(POST): http://localhost:8081/posts/{postId}/images + body;
getById(GET): http://localhost:8081/posts/{postId}/images/{imageId};
getAllByPostId(GET): http://localhost:8081/posts/{postId}/images;
deleteFile(DELETE): http://localhost:8081/posts/{postId}/images/{imageName};

FriendRequestController:
sendFriendRequest(POST): http://localhost:8081/friend-requests/send/{receiverId};
acceptFriendRequest(POST): http://localhost:8081/friend-requests/accept/{requestId};
rejectFriendRequest(POST): http://localhost:8081/friend-requests/reject/{requestId};
cancelFriendRequest(POST): http://localhost:8081/friend-requests/cancel/{receiverId};
deleteFriend(DELETE): http://localhost:8081/friend-requests/{friendId};

9.Launch of all the tests:
EditConfiguration -> JUnit -> name:mvn test -> All In Directory: social-media-api-spring-boot-rest-hibernate-postgresql\src\test ->
Environment variables : clean test

Controller tests: PostControllerTest
Converter tests: PostConverterImplTest
Dao tests: PostDAOImplTest
ServiceTests: PostServiceImplTest

