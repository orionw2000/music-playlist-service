package com.amazon.ata.music.playlist.service.converters;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.models.SongModel;

import java.util.ArrayList;
import java.util.List;

public class ModelConverter {
    /**
     * Converts a provided {@link Playlist} into a {@link PlaylistModel} representation.
     * @param playlist the playlist to convert
     * @return the converted playlist
     */
    public PlaylistModel toPlaylistModel(Playlist playlist) {
        return PlaylistModel.builder()
                .withId(playlist.getId())
                .withName(playlist.getName())
                .withCustomerId(playlist.getCustomerId())
                .withSongCount(playlist.getSongCount())
                .withTags(new ArrayList<>(playlist.getTags()))
                .build();
    }
    public SongModel toSongModel(AlbumTrack track2){
        return SongModel.builder()
                .withAsin(track2.getAsin())
                .withTrackNumber(track2.getTrackNumber())
                .withTitle(track2.getSongTitle())
                .withAlbum(track2.getAlbumName())
                .build();
    }
    public static List<SongModel> toSongModelList(List<AlbumTrack> track2){
        List<SongModel> models = new ArrayList<>();
        for(AlbumTrack track : track2) {
            models.add(SongModel.builder()
                    .withAsin(track.getAsin())
                    .withTrackNumber(track.getTrackNumber())
                    .withTitle(track.getSongTitle())
                    .withAlbum(track.getAlbumName())
                    .build());
        }
        return models;
    }
}
