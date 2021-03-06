package com.fasterxml.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.sort.ExampleDTO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * http://wiki.fasterxml.com/JacksonInFiveMinutes
 */
public class JacksonTest {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonTest.class);

    protected ObjectMapper getJaxbMapper() {
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector intr = new JaxbAnnotationIntrospector(mapper.getTypeFactory());
        mapper.setAnnotationIntrospector(intr);
        return mapper;
    }

    @Test
    public void testUnmarshalToPojo() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\",\"cost\":123}";

        MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
        assertEquals("some product", pojo.getProduct());
        assertEquals((Long) 123L, (Long) pojo.getCost());
    }

    @Test
    public void testUnmarshalToPojoWithImplicitStringToLongConversion() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"cost\":\"123\"}";

        MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
        assertEquals((Long) 123L, (Long) pojo.getCost());
    }

    @Test
    public void testUnmarshalToPojoWithMissingFieldInJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\"}";

        MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
        assertEquals("some product", pojo.getProduct());
        assertNull(pojo.getCost());
    }

    @Test
    public void testUnmarshalToPojoWithUnknownFieldInJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\",\"unknown_field\":123}";

        try {
            @SuppressWarnings("unused")
            MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
            fail();
        } catch (UnrecognizedPropertyException ex) {
        }

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
        assertEquals("some product", pojo.getProduct());
        assertNull(pojo.getCost());
    }

    @Test
    public void testUnmarshalToPojoWithUnknownFieldInJsonJaxBMapper() throws IOException {
        ObjectMapper mapper = getJaxbMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\",\"unknown_field\":123}";

        try {
            @SuppressWarnings("unused")
            MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
            fail();
        } catch (UnrecognizedPropertyException ex) {
        }

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
        assertEquals("some product", pojo.getProduct());
        assertNull(pojo.getCost());
    }

    @Test
    public void testUnmarshalToPojoWithMissingFieldInPojo() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\",\"cost\":123,\"nopojofield\":456}";

        try {
            @SuppressWarnings("unused")
            MyPojo pojo = mapper.readValue(jsonInput, MyPojo.class);
            fail("should have thrown Exception");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testUnmarshalToPojoAndMarshall() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        MyPojo pojoInstance = new MyPojo();
        pojoInstance.setProduct("some product2");
        pojoInstance.setCost(124L);
        String jsonOutput = mapper.writeValueAsString(pojoInstance);
        MyPojo pojoInstance2 = mapper.readValue(jsonOutput, MyPojo.class);
        assertEquals("some product2", pojoInstance2.getProduct());
        assertEquals((Long) 124L, (Long) pojoInstance2.getCost());
    }

    @Test
    public void testMarshalWithAnnotations() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        MyPojoWithAnnotations pojoInstance = new MyPojoWithAnnotations();
        pojoInstance.setProduct("some product2");
        pojoInstance.setCost(124L);
        String jsonOutput = mapper.writeValueAsString(pojoInstance);
        System.out.println(jsonOutput);
        assertTrue(jsonOutput.indexOf("PRODUCTDESC") != -1);
    }

    @Test
    public void testMarshalWithXmlAnnotations() throws IOException {
        ObjectMapper mapper = getJaxbMapper(); // can reuse, share globally

        MyPojoWithXmlAnnotations pojoInstance = new MyPojoWithXmlAnnotations();
        pojoInstance.setProduct("some product2");
        pojoInstance.setCost(124L);
        String jsonOutput = mapper.writeValueAsString(pojoInstance);
        System.out.println(jsonOutput);
        assertTrue(jsonOutput.indexOf("PRODUCTDESC") != -1);
    }

    @Test
    public void testMarshalWithXmlAnnotationsAndSomeNullValue() throws IOException {
        ObjectMapper mapper = getJaxbMapper(); // can reuse, share globally
        // this was the default for versions at least up to 2.4.3:
        mapper.setSerializationInclusion(Include.NON_NULL);

        MyPojoWithXmlAnnotations pojoInstance = new MyPojoWithXmlAnnotations();
        pojoInstance.setSometestattr(null);
        String jsonOutput = mapper.writeValueAsString(pojoInstance);
        System.out.println(jsonOutput);
        assertTrue(jsonOutput.indexOf("sometestattr") == -1);

        // try again with non-null value:
        pojoInstance = new MyPojoWithXmlAnnotations();
        pojoInstance.setSometestattr("asd");
        jsonOutput = mapper.writeValueAsString(pojoInstance);
        System.out.println(jsonOutput);
        assertTrue(jsonOutput.indexOf("sometestattr") != -1);

        // try again with empty String:
        pojoInstance = new MyPojoWithXmlAnnotations();
        pojoInstance.setSometestattr("");
        jsonOutput = mapper.writeValueAsString(pojoInstance);
        System.out.println(jsonOutput);
        assertTrue(jsonOutput.indexOf("sometestattr") != -1);
    }

    @Test
    public void testPojoWithPublicFields() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        String jsonInput = "{\"product\":\"some product\",\"cost\":123}";

        MyPojoPublicFields pojo = mapper.readValue(jsonInput, MyPojoPublicFields.class);
        assertEquals("some product", pojo.product);
        assertEquals((Long) 123L, (Long) pojo.cost);
    }

    @Test
    public void testMarshalToUTF8() throws IOException {
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally

        MyPojo pojoInstance = new MyPojo();
        pojoInstance.setProduct("öäü");
        pojoInstance.setCost(1L);

        // check assumptions
        assertEquals(3, pojoInstance.getProduct().length());
        assertEquals(6, pojoInstance.getProduct().getBytes(Charset.forName("UTF8")).length);

        String jsonOutput = mapper.writeValueAsString(pojoInstance);
        assertEquals(3, jsonOutput.getBytes(Charset.forName("UTF8")).length - jsonOutput.length());
    }

    public static final long DURATION_MS = 1 * 1000L;

    @Test
    public void testPerformance() throws IOException, ClassNotFoundException {
        JsonFactory jsonFactory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        ObjectWriter ow = mapper.writerFor(ExampleDTO.class);
        long n = 0;
        byte[] data;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // JsonGenerator jGenerator = jsonFactory
            // .createGenerator(baos, JsonEncoding.UTF8);
            long end = System.currentTimeMillis() + DURATION_MS;
            do {
                ow.writeValue(baos, ExampleDTO.genRandom());
                n++;
            } while (System.currentTimeMillis() < end);
            LOG.info(String.format("%d objects encoded per second, %d bytes per object",
                n * 1000l / DURATION_MS, baos.size() / n));

            data = baos.toByteArray();

            long start = 0;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
                JsonParser parser = jsonFactory.createParser(bais);
                parser.setCodec(mapper);
                long n2 = 0;
                start = System.currentTimeMillis();
                do {
                    n2++;
                } while (JsonToken.START_OBJECT.equals(parser.nextToken())
                    && parser.readValueAs(ExampleDTO.class) != null);
                assertEquals(n2 - 1, n);
            } catch (EOFException ex) {
            }
            long duration = System.currentTimeMillis() - start;
            LOG.info(String.format("%d objects decoded per second",
                n * 1000l / duration));
        }
    }
}
