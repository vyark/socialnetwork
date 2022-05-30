import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.SneakyThrows;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocialNetworkTest {
    private MongoClient mongo;
    private MongoCredential credential;
    private MongoDatabase database;
    private MongoCollection<Document> user;
    private MongoCollection<Document> friendship;
    private List<Document> userDocuments;
    private List<Document> friendshipDocuments;

    @BeforeEach
    public void init() {
        mongo = MongoClients.create("mongodb://localhost" + ":27017/test");

        credential = MongoCredential.createCredential("user", "database", "password".toCharArray());
        database = mongo.getDatabase("database");
        user = database.getCollection("user");
        user.drop();
        friendship = database.getCollection("friendship");
        friendship.drop();
        insertData();
    }

    /*Average number of messages by day of week.*/
    @SneakyThrows
    @Test
    public void averageNumberOfMessagesByDayOfWeek() {
        List results = new ArrayList<>();
        Document unwind = Document.parse("{\n" + "        \"$unwind\" : " + "\"$messages\"\n" +
                "    }");
        Document addFields = Document.parse("{\"$addFields\": " + "{creation_date: " +
                "{\"$dateToString\": " + "{\"date\": " + "{\"$toDate\": " + "\"$messages" +
                ".creation_date\"}," + "\"format\":" + " " + "\"%Y-%m-%d" + "\"}}}}");
        Document group1 =
                Document.parse("{\n" + "    " + "\"$group\": {\n" + "      \"_id\": " +
                        "\"$creation_date" + "\",\n" + "  " + "    \"count\": {\n" + "        " +
                        "\"$sum\":" + " 1\n" + "      }\n" + "    " + "}\n" + "  }");
        Document addFields2 = Document.parse("{\n" + "   " + " \"$addFields\": {\n" + "      " +
                "\"creation_date\": " + "{\n" + "  " + "      $dayOfWeek: {\n" + "          " +
                "\"$toDate\": " + "\"$_id\"\n" + "        }\n" + "      }\n" + "    " + "}\n" +
                "  }");
        Document project =
                Document.parse("{\n" + "    " + "\"$project\": {\n" + "      _id: 0," + "\n" + " " +
                        "     weekDay: " + "\"$_id\",\n" + "      average: 1\n" + " " + "   }\n" + "  }");
        Document group2 =
                Document.parse("{\n" + "    " + "\"$group\": {\n" + "      \"_id\": " +
                        "\"$creation_date" + "\",\n" + "  " + "    \"average\": {\n" + "        " + "\"$avg\": \"$value\"\n" + "      }\n" + "    " + "}\n" + "  }");
        user.aggregate(Arrays.asList(unwind, addFields, group1, addFields2, group2, project)).into(results);

        System.out.println(results);
        assertEquals(2, results.size());
    }

    /*Max number of new friendships from month to month.*/
    @Test
    public void maxNumberOfNewFriendshipsFromMonthToMonth() {
        List results = new ArrayList<>();
        Document addFields1 = Document.parse("{\n" + "   " + " \"$addFields\": {\n" + "      " +
                "creation_date" + ": {\n" + "      " + "  \"$dateToString\": {\n" + "         " + " \"date\": {\n" + "          " + "  " + "\"$toDate\": " + "\"$creation_date\"\n" + "          },\n" + "          \"format\": " + "\"%Y-%m-%d\"\n" + "     " + "   }\n" + "      }\n" + "    }\n" + "  }");
        Document group1 =
                Document.parse("{\n" + "    " + "\"$group\": {\n" + "      \"_id\": " +
                        "\"$creation_date" + "\",\n" + "  " + "    \"value\": {\n" + "        " + "\"$sum\": \"$value\"\n" + "      }\n" + "    " + "}\n" + "  }");
        Document addFields2 = Document.parse("{\n" + "   " + " \"$addFields\": {\n" + "      " +
                "\"creation_date\": " + "{\n" + "  " + "      $month: {\n" + "          " +
                "\"$toDate\": \"$_id\"\n" + "        }\n" + "  " + "  " + "  }\n" + "    }\n" +
                "  }");
        Document project =
                Document.parse("{\n" + "    \"$project\":" + " {\n" + "      _id: 0," + "\n" + " " +
                        "  " + "   " + "monthNumber: " + "\"$_id" + "\",\n" + "      average: " +
                        "1\n" + "    }\n" + "  }");
        Document group2 =
                Document.parse("{\n" + "    " + "\"$group\": {\n" + "      \"_id\": " +
                        "\"$creation_date" + "\",\n" + "  " + "    \"average\": {\n" + "        " + "\"$max\": \"$value\"\n" + "      }\n" + "    " + "}\n" + "  }");
        friendship.aggregate(Arrays.asList(addFields1, group1, addFields2, group2, project)).into(results);

        System.out.println(results);
        assertEquals(2, results.size());
    }

    /*Min number of watched movies by users with more
    than 100 friends.*/
    /**/
    @Test
    public void minNumberOfWatchedMoviesByUsersWithMoreThan100Friends() {
        List results = new ArrayList<>();
        Document lookup = Document.parse(" { $lookup:\n  " + "      {\n           from: \"user\"," +
                "\n   " + "        " + "localField: " + "\"user_id\",\n           foreignField:" + " " + "\"user_sender_id\",\n          " + " as: " + "\"user\"\n        }\n  " + "  " + "}");
        Document match =
                Document.parse("{\"$match" + "\":{\"movies.is_watched\":{\"$eq\":true" + "}}}");
        Document group_count_user_id = Document.parse(" " + "{\n" + "      " + " $group:\n" + "  "
                + "       {\n" + "     " + "   " + "   _id: \"$user_id\",\n" + "       " + "    " +
                "count: { $count: { } }\n" + "       " + "  " + "}\n" + "     }");
        Document group_min_number =
                Document.parse(" {\n" + "      " + " $group:\n" + "         " + "{\n" + "        "
                        + "   _id: \"$_id\",\n" + "           " + "minNumber: { $min: \"$count\" " +
                        "}" + "  " + "       }\n" + "  " + "   }");
        user.aggregate(Arrays.asList(lookup, match, group_count_user_id, group_min_number)).into(results);
        System.out.println(results);
    }

    @SneakyThrows
    private void insertData() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy" + "-MM-dd");
        userDocuments = Arrays.asList(new Document("user_id", 1).append("name", "John").append(
                "surname", "Smith").append("date_of_birth", df.parse("1989-04-11")).append("city"
                , "Krakow").append("value", 4).append("movies", Arrays.asList(new Document(
                        "movie_id", 1).append("title", "AAAAA").append("description",
                        "aaa aaa " + "aaaaaa").append("year", 1995).append("is_watched", false),
                new Document("movie_id", 2).append("title", "BBBBBB").append("description", "bbb "
                        + "bbbb " + "bbbbbbb").append("year", 1988).append("is_watched", true)))
                .append("audio_tracks", Arrays.asList(new Document("audio_track_id", 1).append("title", "CCCCC")
                 .append("genre", "ddddddd"), new Document("audio_track_id", 2).append("title", "EEEEEEE").append("genre", "fffffffff"))).append("messages", Arrays.asList(new Document("message_id", 1).append("title", "GGGGGG").append("text", "KKKKKKKK").append("creation_date", df.parse("2022-04-15")).append("sender_id", 1).append("receiver_id", 2), new Document("message_id", 2).append("title", "EEEEEEE").append("text", "vvvvvvvvv").append("creation_date", df.parse("2022-04-21")).append("sender_id", 1).append("receiver_id", 3))), new Document("user_id", 2).append("name", "Maya").append("surname", "Rudolph").append("date_of_birth", df.parse("1978-06-10")).append("city", "Paris").append("value", 5).append("messages", Arrays.asList(new Document("message_id", 3).append("title", "GGGGGG").append("text", "KKKKKKKK").append("creation_date", df.parse("2022-04-15")).append("sender_id", 1).append("receiver_id", 2))), new Document("user_id", 3).append("name", "Kate").append("surname", "Guly").append("date_of_birth", df.parse("1990-11-02")).append("city", "Brest").append("value", 9).append("messages", Arrays.asList(new Document("message_id", 4).append("title", "EEEEEEE").append("text", "vvvvvvvvv").append("creation_date", df.parse("2022-04-21")).append("sender_id", 1).append("receiver_id", 3))));
        user.insertMany(userDocuments);

        friendshipDocuments = Arrays.asList(new Document("friendship_id", 1).append(
                "user_sender_id", 1).append("user_receiver_id", 2).append("value", 18).append(
                "creation_date", df.parse("2022-04-15")), new Document("friendship_id",
                2).append("user_sender_id", 1).append("user_receiver_id", 3).append("value", 5).append("creation_date", df.parse("2022-05-21")));
        friendship.insertMany(friendshipDocuments);
    }
}