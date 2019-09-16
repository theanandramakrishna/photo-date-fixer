package com.anand.photoedit;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPagedResponse;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;

public class AlbumEditor
{
    Album _album;
    PhotosLibraryClient _client;

    public AlbumEditor(Album album, PhotosLibraryClient client)
    {
        _album = album;
        _client = client;
    }

    public void edit()
    {
        SearchMediaItemsPagedResponse response = _client.searchMediaItems(_album.getId());

        for (MediaItem mediaItem : response.iterateAll())
        {
            
        }
    }
}