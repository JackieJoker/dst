package dst.ass1.doc.tests;

import dst.ass1.doc.DocumentTestData;
import dst.ass1.doc.EmbeddedMongo;
import dst.ass1.doc.MongoService;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class Ass1_4_2bTest {

    @ClassRule
    public static EmbeddedMongo embeddedMongo = new EmbeddedMongo();

    @Rule
    public MongoService mongo = new MongoService(new DocumentTestData());

    @Test
    public void findIdsByNameAndRadius_returnsCorrectLocationIds() throws Exception {
        List<Long> locationIds = mongo.getDocumentQuery().findIdsByNameAndRadius("McD", 16.367873, 48.198763, 1000);
        assertFalse(locationIds.isEmpty());
        assertThat(locationIds, hasItems(2L, 3L, 4L));
    }

    @Test
    public void findIdsByType_withNonExistingType_returnsNoLocationIds() throws Exception {
        List<Long> locationIds = mongo.getDocumentQuery().findIdsByNameAndRadius("McD", 16.367873, 48.198763, 5);
        assertNotNull(locationIds);
        assertTrue(locationIds.isEmpty());

        locationIds = mongo.getDocumentQuery().findIdsByNameAndRadius("NONEXISTING", 16.367873, 48.198763, 2000);

        assertNotNull(locationIds);
        assertTrue(locationIds.isEmpty());
    }

}
