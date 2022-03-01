package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class Ass1_4_3_02Test {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @Test
    public void getDocumentStatistics_returnsCorrectStatistics() throws Exception {
        List<Document> documentStatistics = mongo.getDocumentQuery().getDocumentStatistics();
        assertNotNull(documentStatistics);
        assertEquals(3, documentStatistics.size());

        List<String> types = documentStatistics.stream().map(d -> d.getString("_id")).collect(Collectors.toList());
        assertThat("expected three aggregation keys", types, hasItems("Restaurant", "Park", "University"));

        Map<String, Integer> dsMap = documentStatistics.stream().collect(Collectors.toMap(
            d -> d.getString("_id"),
            d -> d.getInteger("value"))
        );

        assertEquals(4, dsMap.get("Restaurant"), 0);
        assertEquals(1, dsMap.get("Park"), 0);
        assertEquals(7, dsMap.get("University"), 0);
    }

}
