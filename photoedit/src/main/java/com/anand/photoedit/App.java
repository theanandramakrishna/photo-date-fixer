package com.anand.photoedit;

import java.security.GeneralSecurityException;
import java.util.*;
import java.io.*;



import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.UserCredentials;
import com.google.auth.Credentials;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.ListAlbumsPagedResponse;
import com.google.photos.types.proto.Album;

/**
 * Hello world!
 *
 */
public class App 
{
    static PhotosLibraryClient _client;
    static AlbumEditor _albumEditor;

    private static Credentials getCredentials(FileInputStream st) 
        throws IOException, GeneralSecurityException
    {
        GoogleClientSecrets clientSecrets =
          GoogleClientSecrets.load(
            JacksonFactory.getDefaultInstance(), new InputStreamReader(st));
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();
        
        GoogleAuthorizationCodeFlow flow =
          new GoogleAuthorizationCodeFlow.Builder(
                  GoogleNetHttpTransport.newTrustedTransport(),
                  JacksonFactory.getDefaultInstance(),
                  clientSecrets,
                  Arrays.asList("https://www.googleapis.com/auth/photoslibrary.readonly"))
              .setAccessType("offline")
              .build();
        LocalServerReceiver receiver =
            new LocalServerReceiver.Builder().build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return UserCredentials.newBuilder()
            .setClientId(clientId)
            .setClientSecret(clientSecret)
            .setRefreshToken(credential.getRefreshToken())
            .build();
    }

    private static void initializeClient(FileInputStream streamCreds) 
        throws IOException, GeneralSecurityException
    {
        Credentials credential = getCredentials(streamCreds); 
        PhotosLibrarySettings settings = 
            PhotosLibrarySettings.newBuilder().setCredentialsProvider(
                FixedCredentialsProvider.create(credential))
                .build();
        
        _client = PhotosLibraryClient.initialize(settings);

    }

    private static void initializeAlbumEditor()
    {
        ListAlbumsPagedResponse albums = _client.listAlbums();

        for (Album album : albums.iterateAll())
        {
            if (album.getTitle().equals("Family Videos"))
            {
                _albumEditor = new AlbumEditor(album, _client);
                return;
            }
        }

        throw new RuntimeException("Album not found");
    }
    public static void main( String[] args )
    {
        try
        {
            initializeClient(new FileInputStream("credentials.json"));

            initializeAlbumEditor();

            _albumEditor.edit();

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally 
        {
            if (_client != null)
            {
                _client.close();
            }
        }

    }
}
