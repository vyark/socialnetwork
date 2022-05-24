import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import lombok.SneakyThrows;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Accumulators.max;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.lookup;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocialNetworkTest {
    private MongoClient mongo;
    private MongoCredential credential;
    private MongoDatabase database;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> friendshipCollection;
    private List<Document> userDocuments;
    private List<Document> friendshipDocuments;

    @BeforeEach
    public void init() {
        mongo = MongoClients.create("mongodb://localhost:27017/test");

        credential = MongoCredential.createCredential("user", "database",
                "password".toCharArray());
        database = mongo.getDatabase("database");
        userCollection = database.getCollection("user");
        userCollection.drop();
        friendshipCollection = database.getCollection("friendship");
        friendshipCollection.drop();
        insertData();
    }

    /*Average number of messages by day of week.*/
    @Test
    public void averageNumberOfMessagesByDayOfWeek() {
        List results = new ArrayList<>();
        Document addFields1 = Document.parse("{\n" +
                "    \"$addFields\": {\n" +
                "      date_of_birth: {\n" +
                "        \"$dateToString\": {\n" +
                "          \"date\": {\n" +
                "            \"$toDate\": \"$date_of_birth\"\n" +
                "          },\n" +
                "          \"format\": \"%Y-%m-%d\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }");
        Document group1 = Document.parse("{\n" +
                "    \"$group\": {\n" +
                "      \"_id\": \"$date_of_birth\",\n" +
                "      \"value\": {\n" +
                "        \"$sum\": \"$value\"\n" +
                "      }\n" +
                "    }\n" +
                "  }");
        Document addFields2 = Document.parse("{\n" +
                "    \"$addFields\": {\n" +
                "      \"date_of_birth\": {\n" +
                "        $dayOfWeek: {\n" +
                "          \"$toDate\": \"$_id\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }");
        Document project = Document.parse("{\n" +
                "    \"$project\": {\n" +
                "      _id: 0,\n" +
                "      weekDay: \"$_id\",\n" +
                "      average: 1\n" +
                "    }\n" +
                "  }");
        Document group2 = Document.parse("{\n" +
                "    \"$group\": {\n" +
                "      \"_id\": \"$date_of_birth\",\n" +
                "      \"average\": {\n" +
                "        \"$avg\": \"$value\"\n" +
                "      }\n" +
                "    }\n" +
                "  }");
        userCollection.aggregate(Arrays.asList(
                              addFields1,
                              group1,
                              addFields2,
                              group2, project))
                      .into(results);

        System.out.println(results);
        assertEquals(3, results.size());
    }

    /*Max number of new friendships from month to month.*/
    @Test
    public void maxNumberOfNewFriendshipsFromMonthToMonth() {
        List results = new ArrayList<>();
        friendshipCollection.aggregate(Arrays.asList(group("$creation_date", max(
                "friendship_id", 1)))).into(results);
        System.out.println(results);
        assertEquals(results.size(), 2);
    }

    /*Min number of watched movies by users with more than 100 friends.*/
    /**/
    @Test
    public void MinNumberOfWatchedMoviesByUsersWithMoreThan100Friends() {
        List results = new ArrayList<>();
        userCollection.aggregate(Arrays.asList(lookup("friendship", "user_id", "user_sender_id", "user_friends").toBsonDocument(
                BsonDocument.class,
                MongoClientSettings.getDefaultCodecRegistry())));
        userCollection.find(eq("movies.is_watched", true))
                      .projection(Projections.fields(Projections.include("movies.$"))).into(results);
        System.out.println(results);
    }

    @SneakyThrows
    private void insertData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        userDocuments = Arrays.asList(new Document("user_id", 1)
                        .append("name", "John")
                        .append("surname", "Smith")
                        .append("date_of_birth", df.parse("1989-04-11"))
                        .append("city", "Krakow")
                        .append("value", 4)
                        .append("movies", Arrays.asList(new Document("movie_id", 1)
                                        .append("title", "AAAAA")
                                        .append("description", "aaa aaa aaaaaa")
                                        .append("year", 1995)
                                        .append("is_watched", false),
                                new Document("movie_id", 2)
                                        .append("title", "BBBBBB")
                                        .append("description", "bbb bbbb bbbbbbb")
                                        .append("year", 1988)
                                        .append("is_watched", true)))
                        .append("audio_tracks", Arrays.asList(new Document("audio_track_id", 1)
                                        .append("title", "CCCCC")
                                        .append("genre", "ddddddd"),
                                new Document("audio_track_id", 2)
                                        .append("title", "EEEEEEE")
                                        .append("genre", "fffffffff")))
                        .append("messages", Arrays.asList(new Document("message_id", 1)
                                        .append("title", "GGGGGG")
                                        .append("text", "KKKKKKKK")
                                        .append("creation_date", df.parse("2022-04-15"))
                                        .append("sender_id", 1)
                                        .append("receiver_id", 2),
                                new Document("message_id", 2)
                                        .append("title", "EEEEEEE")
                                        .append("text", "vvvvvvvvv")
                                        .append("creation_date", df.parse("2022-04-21"))
                                        .append("sender_id", 1)
                                        .append("receiver_id", 3))),
                new Document("user_id", 2)
                        .append("name", "Maya")
                        .append("surname", "Rudolph")
                        .append("date_of_birth", df.parse("1978-06-10"))
                        .append("city", "Paris")
                        .append("value", 5)
                        .append("messages", Arrays.asList(new Document("message_id", 3)
                                .append("title", "GGGGGG")
                                .append("text", "KKKKKKKK")
                                .append("creation_date", df.parse("2022-04-15"))
                                .append("sender_id", 1)
                                .append("receiver_id", 2))),
                new Document("user_id", 3)
                        .append("name", "Kate")
                        .append("surname", "Guly")
                        .append("date_of_birth", df.parse("1990-11-02"))
                        .append("city", "Brest")
                        .append("value", 9)
                        .append("messages", Arrays.asList(
                                new Document("message_id", 4)
                                        .append("title", "EEEEEEE")
                                        .append("text", "vvvvvvvvv")
                                        .append("creation_date", df.parse("2022-04-21"))
                                        .append("sender_id", 1)
                                        .append("receiver_id", 3))));
        userCollection.insertMany(userDocuments);

        friendshipDocuments = Arrays.asList(new Document("friendship_id", 1).append("user_sender_id", 1).append("user_receiver_id",
                        2).append("creation_date", df.parse("2022-04-15")),
                new Document("friendship_id", 2).append("user_sender_id", 1).append("user_receiver_id",
                        3).append("creation_date", df.parse("2022-04-21")));
        friendshipCollection.insertMany(friendshipDocuments);
    }

//    @SneakyThrows
//    @Test
//    public void test() {
//        List<Friendship> list = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            Friendship friendship = new Friendship();
//            friendship.setId(Long.valueOf(i));
//            for (int j = 1; j <= 10; j++) {
//                friendship.setUserSender(Long.valueOf(j));
//                for(int k=1; k<=10; k++) {
//                    friendship.setUserId2((Double.valueOf(Math.floor(Math.random() * (10 - 1 + 1) + 1)).longValue()));
//                    Random rnd = new Random();
//                    Date date = new Date(Math.abs(System.currentTimeMillis() - rnd.nextLong()));
//                    friendship.setCreationDate(Date.from(date.toInstant()));
//                    list.add(friendship);
//                }
//            }
//        }
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(new File("C:\\Users\\Volha_Yarkouskaya\\friendship.json"), list);
//    }
}