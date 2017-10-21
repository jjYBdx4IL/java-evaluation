package com.fasterxml.sort;

import org.apache.tika.io.IOUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class ExampleDTOReader extends DataReader<ExampleDTO> implements AutoCloseable {

    private final InputStream is;
    private final ObjectInputStream ois;
    
    public ExampleDTOReader(InputStream is) throws IOException {
        this.is = is;
        this.ois = new ObjectInputStream(is);
    }
    
    @Override
    public ExampleDTO readNext() throws IOException {
        try {
            return (ExampleDTO) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        } catch (EOFException ex) {
            return null;
        }
    }

    @Override
    public int estimateSizeInBytes(ExampleDTO item) {
        return 100;
    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(ois);
        IOUtils.closeQuietly(is);
    }

    public static class Factory
        extends DataReaderFactory<ExampleDTO> {
            
        @Override
        public DataReader<ExampleDTO> constructReader(InputStream in) throws IOException {
            return new ExampleDTOReader(in);
        }
    }
}
