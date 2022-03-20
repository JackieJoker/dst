package dst.ass1.doc.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import dst.ass1.doc.IDocumentRepository;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.Map;

public class DocumentRepository implements IDocumentRepository {

    public DocumentRepository() {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            createLocationIdIndex(collection);
            createGeoIndex(collection);
        }
    }

    @Override
    public void insert(ILocation location, Map<String, Object> locationProperties) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
            MongoCollection<Document> collection = database.getCollection(Constants.COLL_LOCATION_DATA);

            try {
                collection.insertOne(new Document(locationProperties)
                        .append("location_id",location.getLocationId())
                        .append("name",location.getName())
                );
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }
        }
    }

    private void createLocationIdIndex(MongoCollection<Document> collection) {
        try {
            IndexOptions indexOptions = new IndexOptions().unique(true);
            collection.createIndex(Indexes.descending("location_id"), indexOptions);
        } catch (DuplicateKeyException e) {
            System.out.printf("duplicate field values encountered, couldn't create index: \t%s\n", e);
        }
    }

    private void createGeoIndex(MongoCollection<Document> collection) {
        try {
            collection.createIndex(Indexes.geo2dsphere("geo"));
        } catch (MongoCommandException e) {
            if (e.getErrorCodeName().equals("IndexOptionsConflict"))
                System.out.println("there's an existing text index with different options");
        }
    }
}
