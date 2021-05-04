package tests.java.net;

/*
 * #%L
 * Evaluation
 * %%
 * Copyright (C) 2014 Github jjYBdx4IL Projects
 * %%
 * #L%
 */

import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateException;
import com.github.fge.uritemplate.URITemplateParseException;
import com.github.fge.uritemplate.vars.VariableMap;
import com.github.fge.uritemplate.vars.VariableMapBuilder;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Uplink URL: <a href="https://github.com/fge/uri-template">fge uri-template @ github</a>
 *
 * @author Github jjYBdx4IL Projects
 */
@Ignore
public class UriTemplateTest {

    @Test
    public void testAddScalarValue() throws URITemplateException, MalformedURLException {
        final URITemplate template = new URITemplate("http://localhost/{?arg1}");
        final VariableMapBuilder builder = VariableMap.newBuilder();
        builder.addScalarValue("arg1", "value1");
        final VariableMap vars = builder.freeze();

        assertEquals("http://localhost/?arg1=value1", template.toString(vars));
    }

    @Test
    public void testScalarValueEncoding() throws URITemplateException, MalformedURLException {
        final URITemplate template = new URITemplate("http://localhost/{?arg1}{&arg2}");
        final VariableMapBuilder builder = VariableMap.newBuilder();
        builder.addScalarValue("arg1", "=");
        builder.addScalarValue("arg2", "ö");
        final VariableMap vars = builder.freeze();

        assertEquals("http://localhost/?arg1=%3D&arg2=%C3%B6", template.toString(vars));
    }

    @Test
    public void testAddScalarValueGwtCompatible() throws URITemplateException, MalformedURLException {
        final URITemplate template = new URITemplate("http://localhost/?{&arg1}");
        final VariableMapBuilder builder = VariableMap.newBuilder();
        builder.addScalarValue("arg1", "value1");
        final VariableMap vars = builder.freeze();

        assertEquals("http://localhost/?&arg1=value1", template.toString(vars));
    }

    @Test
    public void testMap() throws URITemplateParseException, URITemplateException {
        final URITemplate template = new URITemplate("http://localhost/{?map*}");
        final VariableMapBuilder builder = VariableMap.newBuilder();
        Map<String, String> map = new HashMap<>();
        map.put("arg1", "=");
        builder.addMapValue("map", map);
        final VariableMap vars = builder.freeze();
        
        assertEquals("http://localhost/?arg1=%3D", template.toString(vars));
    }
}
