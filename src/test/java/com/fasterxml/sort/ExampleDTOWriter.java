package com.fasterxml.sort;

import org.apache.tika.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ExampleDTOWriter extends DataWriter<ExampleDTO> implements AutoCloseable {

    private final OutputStream os;
    private final ObjectOutputStream oos;

    public ExampleDTOWriter(OutputStream os) throws IOException {
        this.os = os;
        this.oos = new ObjectOutputStream(os);
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(oos);
        IOUtils.closeQuietly(os);
    }

    @Override
    public void writeEntry(ExampleDTO item) throws IOException {
        oos.writeObject(item);
    }

    public static class Factory
        extends DataWriterFactory<ExampleDTO> {

        @Override
        public DataWriter<ExampleDTO> constructWriter(OutputStream os) throws IOException {
            return new ExampleDTOWriter(os);
        }
    }

}
