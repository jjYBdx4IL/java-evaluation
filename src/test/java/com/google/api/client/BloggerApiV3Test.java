package com.google.api.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import com.github.jjYBdx4IL.utils.env.Surefire;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.Blogger.Blogs.GetByUrl;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * Inspired by:
 * https://stackoverflow.com/questions/35369919/authenticate-own-google-account-for-blogger-api-with-java
 * 
 * Beware! This test method saves your (blogger) credentials on disk.
 * 
 * @author jjYBdx4IL
 */
public class BloggerApiV3Test {

    private static final Logger LOG = LoggerFactory.getLogger(BloggerApiV3Test.class);
    private static String BLOG_URL = "https://javagc.blogspot.com/";

    @Test
    public void test() throws Exception {
        assumeTrue(Surefire.isSingleTestExecution());
        // JavaUtilLoggingUtils.setJavaNetURLConsoleLoggingLevel(Level.FINEST);

        // Configure the Installed App OAuth2 flow.
        GoogleApiAuth auth = new GoogleApiAuth(BloggerApiV3Test.class);
        Credential credential = auth.authorize(Arrays.asList(BloggerScopes.BLOGGER), "user");
        
        Blogger blogger = new Blogger.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            credential)
            .setApplicationName("APP_NAME")
            .setHttpRequestInitializer(credential)
            .build();

        //
        // identify blog by its' url
        //
        GetByUrl blogGetByUrlAction = blogger.blogs().getByUrl(BLOG_URL);
        blogGetByUrlAction.setFields("description,name,posts/totalItems,updated,id");
        Blog blog = blogGetByUrlAction.execute();

        LOG.info("Name: " + blog.getName());
        LOG.info("Description: " + blog.getDescription());
        LOG.info("Post Count: " + blog.getPosts().getTotalItems());
        LOG.info("Last Updated: " + blog.getUpdated());

        //
        // list posts
        //
        Blogger.Posts.List listPosts = blogger.posts().list(blog.getId());
        listPosts.setFetchBodies(false);
        listPosts.setMaxResults(500l);
        listPosts.setStatus(Arrays.asList("draft", "live", "scheduled"));
        PostList posts = listPosts.execute();

        // LOG.info(posts.toPrettyString());

        int i = 0;
        for (Post post : posts.getItems()) {
            i++;
            LOG.info(i + ": " + post.getStatus() + ": " + post.getTitle() + ", " + post.getPublished());
            LOG.info(post.toPrettyString());
        }

        //
        // publish a new post
        //
        Post newPost = new Post();
        newPost.setTitle("test title");
        newPost.setContent("test content");
        newPost.setCustomMetaData("{\"a\":\"bb\"}");
        Blogger.Posts.Insert insertPost = blogger.posts().insert(blog.getId(), newPost);
        insertPost.setIsDraft(false);
        DateTime now = new DateTime(System.currentTimeMillis());
        LOG.info("started insert request at: " + now);
        Post insertReply = insertPost.execute();
        assertNotNull(insertReply);
        LOG.info("insert time: " + (insertReply.getUpdated().getValue() - now.getValue()) / 1000 + " seconds");
        LOG.info("updated time: " + insertReply.getUpdated());
        LOG.info("published time: " + insertReply.getPublished());

        // LOG.info(insertReply.toPrettyString());
        assertEquals("LIVE", insertReply.getStatus());
        assertEquals(newPost.getTitle(), insertReply.getTitle());
        assertEquals(newPost.getContent(), insertReply.getContent());
        assertNotNull(insertReply.getId());
        assertTrue(Long.valueOf(insertReply.getId()).longValue() > 0);
        assertNotNull(insertReply.getPublished());
        assertNotNull(insertReply.getUpdated());

        //
        // fetch the new post
        //
        Blogger.Posts.Get getPost = blogger.posts().get(blog.getId(), insertReply.getId());
        //getPost.setFields("id,title,content,customMetaData");
        Post retPost = getPost.execute();
        assertNotNull(retPost);
        assertEquals(retPost.getId(), insertReply.getId());
        assertEquals(retPost.getTitle(), insertReply.getTitle());
        assertEquals(retPost.getContent(), insertReply.getContent());
        assertNull(retPost.getCustomMetaData());

        //
        // update the post's content
        //
        retPost.setContent(retPost.getContent() + " abc");
        retPost.setUpdated(new DateTime(0));
        Blogger.Posts.Update updatePost = blogger.posts().update(blog.getId(), retPost.getId(), retPost);
        Post updatedPost = updatePost.execute();
        assertNotNull(updatedPost);
        assertEquals("LIVE", updatedPost.getStatus());
        assertEquals(updatedPost.getTitle(), "test title");
        assertEquals(updatedPost.getContent(), "test content abc");
        assertNotNull(updatedPost.getId());
        assertTrue(Long.valueOf(updatedPost.getId()).longValue() > 0);
        assertNotNull(updatedPost.getPublished());
        assertNotNull(updatedPost.getUpdated());
        // we can't modify the "updated time"
        assertTrue(updatedPost.getUpdated().getValue() > System.currentTimeMillis() - 300000L);

        //
        // fetch the updated post
        //
        Blogger.Posts.Get getPost2 = blogger.posts().get(blog.getId(), insertReply.getId());
        Post retPost2 = getPost2.execute();
        assertNotNull(retPost2);
        assertEquals(retPost2.getId(), insertReply.getId());
        assertEquals(retPost2.getTitle(), "test title");
        assertEquals(retPost2.getContent(), "test content abc");

        //
        // revert to draft status
        //
        Blogger.Posts.Revert revertPost = blogger.posts().revert(blog.getId(), retPost2.getId());
        Post revertedPost = revertPost.execute();
        assertNotNull(revertedPost);
        assertEquals("DRAFT", revertedPost.getStatus());
        assertEquals(revertedPost.getTitle(), "test title");
        assertEquals(revertedPost.getContent(), "test content abc");
        assertNotNull(revertedPost.getId());
        assertTrue(Long.valueOf(revertedPost.getId()).longValue() > 0);
        assertNotNull(revertedPost.getPublished());
        assertNotNull(revertedPost.getUpdated());
        
        //
        // publish again
        //
        Blogger.Posts.Publish publishPost = blogger.posts().publish(blog.getId(), retPost2.getId());
        Post publishedPost = publishPost.execute();
        assertNotNull(publishedPost);
        assertEquals("LIVE", publishedPost.getStatus());
        assertEquals(publishedPost.getTitle(), "test title");
        assertEquals(publishedPost.getContent(), "test content abc");
        assertNotNull(publishedPost.getId());
        assertTrue(Long.valueOf(publishedPost.getId()).longValue() > 0);
        assertNotNull(publishedPost.getPublished());
        assertNotNull(publishedPost.getUpdated());
        
        //
        // delete the post
        //
        Blogger.Posts.Delete delPost = blogger.posts().delete(blog.getId(), insertReply.getId());
        Void result = delPost.execute();
        assertNull(result);

        //
        // try to delete a non-existing post
        //
        Blogger.Posts.Delete delPost2 = blogger.posts().delete(blog.getId(), "1");
        try {
            delPost2.execute();
            fail();
        } catch (IOException ex) {
        }
    }
}
