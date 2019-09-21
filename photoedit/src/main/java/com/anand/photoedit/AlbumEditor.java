package com.anand.photoedit;

import java.util.GregorianCalendar;
import java.text.ParseException;

import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient.SearchMediaItemsPagedResponse;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.photos.types.proto.MediaMetadata;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

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
            String datetimeParts = fileName.substring(0, fileName.indexOf('.'));
            StringBuilder sb = new StringBuilder();

            //
            // Write the string into RFC3339 format so it can be parsed
            //


            for (int i = 0; i < datetimeParts.length(); i++)
            {
                if (datetimeParts.charAt(i) == ' ')
                {
                    sb.append("T");
                }
                else if (datetimeParts.charAt(i) == '_')
                {
                    sb.append(":");
                }
                else 
                {
                    sb.append(datetimeParts.charAt(i));
                }
            }
            sb.append("-08:00");
            
            try 
            {
                Timestamp timestamp = Timestamps.parse(sb.toString());

                //
                // Write the field
                //

                MediaMetadata.Builder builder = metadata.toBuilder();
                builder.setCreationTime(timestamp);
                builder.build();
            }
            catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}