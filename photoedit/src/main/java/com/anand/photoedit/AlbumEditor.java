package com.anand.photoedit;

import java.util.GregorianCalendar;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPagedResponse;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import com.google.protobuf.Timestamp;

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
            MediaMetadata metadata = mediaItem.getMediaMetadata();
            Timestamp createTime = metadata.getCreationTime();

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTimeInMillis(createTime.getSeconds() * 1000);
            if (cal.get(GregorianCalendar.YEAR) != 2019)
            {
                continue;
            }

            //
            // We found an item with a create date to this year.  Needs to be edited
            //

            String fileName = mediaItem.getFilename();

            
        }
    }
}