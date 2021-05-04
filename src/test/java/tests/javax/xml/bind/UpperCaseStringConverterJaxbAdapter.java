package tests.javax.xml.bind;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class UpperCaseStringConverterJaxbAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return v.toUpperCase(Locale.ROOT);
    }

    @Override
    public String marshal(String v) throws Exception {
        return v.toUpperCase(Locale.ROOT);
    }
}