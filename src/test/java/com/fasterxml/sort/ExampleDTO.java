package com.fasterxml.sort;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class ExampleDTO implements Serializable {

    private static final long serialVersionUID = -7641925131534714419L;

    private Date created;
    private String subject;
    private String content;

    public ExampleDTO() {

    }

    public ExampleDTO(Date date, String subject, String content) {
        this.created = date;
        this.subject = subject;
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExampleDTO [created=");
        builder.append(created);
        builder.append(", subject=");
        builder.append(subject);
        builder.append(", content=");
        builder.append(content);
        builder.append("]");
        return builder.toString();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private static Random r = new Random();
    public static ExampleDTO genRandom() {
        return new ExampleDTO(
            new Date((long) r.nextInt((int) (System.currentTimeMillis() / 1000l))),
            "subject " + r.nextInt(),
            "content " + r.nextLong());
    }
}
