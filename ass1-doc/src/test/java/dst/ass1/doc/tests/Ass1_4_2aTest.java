package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.doc.MongoService;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class Ass1_4_2aTest {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @SuppressWarnings("unchecked")
    @Test
    public void findByLocationId_returnsCorrectDocument() {
        IDocumentQuery documentQuery = mongo.getDocumentQuery();

        Document location = documentQuery.findLocationById(2L);
        assertNotNull(location);
        assertEquals(2L, (long) location.getLong(Constants.I_LOCATION));
        assertEquals("McDonald's", location.getString(Constants.M_LOCATION_NAME));
        assertEquals("place", location.getString("type"));
        assertEquals("Restaurant", location.getString("category"));

        Document geo = location.get("geo", Document.class);
        assertNotNull(geo);
        List<Double> coordinates = geo.getList("coordinates", Double.class);
        assertNotNull(coordinates);
        assertEquals(2, coordinates.size());
        assertEquals("Point", geo.getString("type"));
        assertEquals(16.368528, coordinates.get(0), 0.0);
        assertEquals(48.200939, coordinates.get(1), 0.0);
    }

    @Test
    public void findByLocationId_withInvalidId_returnsNull() throws Exception {
        Document material = mongo.getDocumentQuery().findLocationById(1337L);
        assertNull(material);
    }

}
