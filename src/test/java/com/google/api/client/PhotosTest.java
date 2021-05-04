package com.google.api.client;

import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.env.CI;
import com.github.jjYBdx4IL.utils.env.Maven;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPage;
import com.google.photos.library.v1.proto.Album;
import com.google.photos.library.v1.proto.MediaItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

public class PhotosTest {

    private static final File TEMP_DIR = Maven.getTempTestDir(PhotosTest.class);
    private static final List<String> REQUIRED_SCOPES = ImmutableList.of(
        "https://www.googleapis.com/auth/photoslibrary.readonly",
        "https://www.googleapis.com/auth/photoslibrary.appendonly");

    @Test
    public void test() throws Exception {
        assumeFalse(CI.isCI());
        assumeFalse(SystemUtils.IS_OS_WINDOWS);

        GoogleApiAuth auth = new GoogleApiAuth(PhotosTest.class);
        Credential credential = auth.authorize(REQUIRED_SCOPES, "user");

        // Set up the Photos Library Client that interacts with the API
        PhotosLibrarySettings settings = PhotosLibrarySettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(auth.toUserCredentials(credential)))
            .build();

        PhotosLibraryClient plc = PhotosLibraryClient.initialize(settings);

        String albumId = null;
        for (Album album : plc.listAlbums().iterateAll()) {
            albumId = album.getId();
            for (MediaItem item : plc.searchMediaItems(albumId).iterateAll()) {
                if ("ball-407081__340.jpg".equals(item.getFilename())) {
                    FileUtils.copyURLToFile(new URL(item.getBaseUrl() + "=w640-h480"), new File(TEMP_DIR, "ball.jpg"));
                    // @insert:image:ball.jpg@
                }
            }
            SearchMediaItemsPage page = plc.searchMediaItems(albumId).getPage();
            int i = 0;
            int j = 0;
            while (page != null) {
                System.out.println("page " + i + " (" + page.getPageElementCount() + "):");
                i++;
                for (MediaItem item : page.getValues()) {
                    System.out.println(j + " " + item.getFilename());
                    j++;
                }
                page = page.getNextPage();
            }
        }

        plc.close();
    }
}
