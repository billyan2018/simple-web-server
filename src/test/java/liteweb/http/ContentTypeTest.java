package liteweb.http;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentTypeTest {

	@Test
	public void getCorrectContentTypeByExtension() {
		assertEquals(ContentType.CSS, ContentType.valueOf("CSS"));
		assertEquals(ContentType.GIF, ContentType.valueOf("GIF"));
		assertEquals(ContentType.HTM, ContentType.valueOf("HTM"));
		assertEquals(ContentType.HTML, ContentType.valueOf("HTML"));
		assertEquals(ContentType.ICO, ContentType.valueOf("ICO"));
		assertEquals(ContentType.JPG, ContentType.valueOf("JPG"));
		assertEquals(ContentType.JPEG, ContentType.valueOf("JPEG"));
		assertEquals(ContentType.PNG, ContentType.valueOf("PNG"));
		assertEquals(ContentType.TXT, ContentType.valueOf("TXT"));
		assertEquals(ContentType.XML, ContentType.valueOf("XML"));
	}

}
