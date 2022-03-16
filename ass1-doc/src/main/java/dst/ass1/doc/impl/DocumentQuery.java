package dst.ass1.doc.impl;

import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;

public class DocumentQuery implements IDocumentQuery {
    @Override
    public Document findLocationById(Long locationId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            FindIterable<Document> result = collection.find(eq("location_id", locationId));
            return result.first();
        }
    }

    @Override
    public List<Long> findIdsByNameAndRadius(String name, double longitude, double latitude, double radius) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            //potential security problem
            //to avoid sql injection due to regex
            //we should validate the String "name"
            Point x = new Point(new Position(longitude, latitude));
            FindIterable<Document> result = collection
                    .find(
                            and(
                                    regex("name", name, "i"),
                                    nearSphere("geo", x, radius, null)
                            ))
                    .projection(Projections
                            .fields(Projections.include("location_id"),
                                    Projections.excludeId()));
            MongoCursor<Document> iterator =  result.iterator();
            List<Long> ids = new ArrayList<>();
            while(iterator.hasNext()) {
                ids.add(iterator.next().getLong("location_id"));
            }
            return ids;
        }
    }

    @Override
    public List<Document> getDocumentStatistics() {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            return null;
        }
    }
}
