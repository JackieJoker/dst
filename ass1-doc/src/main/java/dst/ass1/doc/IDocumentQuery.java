package dst.ass1.doc;

import org.bson.Document;

import java.util.List;

public interface IDocumentQuery {

    Document findLocationById(Long locationId);

    List<Long> findIdsByNameAndRadius(String name, double longitude, double latitude, double radius);

    List<Document> getDocumentStatistics();

}
