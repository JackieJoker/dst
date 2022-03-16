package dst.ass1.doc.impl;

import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.*;

public class DocumentQuery implements IDocumentQuery {
    private static final Logger LOGGER = Logger.getLogger(DocumentQuery.class.getName());

    public DocumentQuery() {
        LOGGER.addHandler(new ConsoleHandler());
    }

    @Override
    public Document findLocationById(Long locationId) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            FindIterable<Document> result = collection.find(eq("location_id", locationId));

            LOGGER.info("Query findLocationById(locationId: " +  locationId + ") executed. " + (result.first() == null ? "0 " : "1 ") + "result(s). Output: " + result.first() + ".");
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
            List<Long> ids = result
                    .map(document -> document.getLong("location_id"))
                    .into(new ArrayList<>());
            LOGGER.info("Query findIdsByNameAndRadius(name: " + name + ", longitude: " + longitude + ", latitude: " + latitude + ") executed. " +
                     + ids.size() + " result(s). Output: " + ids + ".");
            return ids;
        }
    }

    @Override
    public List<Document> getDocumentStatistics() {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            AggregateIterable<Document> result = collection.aggregate(
                    Arrays.asList(
                            Aggregates.match(Filters.eq("type", "place")),
                            Aggregates.group("$category", Accumulators.sum("value", 1))
                    )
            );
            List<Document> statistics = result.into(new ArrayList<>());
            LOGGER.info("Query getDocumentStatistics() executed. " + statistics.size() + " result(s). Output: " + statistics + ".");
            return statistics;
        }
    }
}
