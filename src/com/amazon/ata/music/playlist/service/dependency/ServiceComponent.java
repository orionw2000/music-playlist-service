package com.amazon.ata.music.playlist.service.dependency;

import com.amazon.ata.music.playlist.service.activity.*;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {DaoModule.class})
public interface ServiceComponent {
   // PlaylistDao providePlaylistDao();
   // AlbumTrackDao provideAlbumTrackDao();
    AddSongToPlaylistActivity provideAddSongToPlaylistActivity();
    UpdatePlaylistActivity provideUpdatePlaylistActivity();
    GetPlaylistActivity provideGetPlaylistActivity();
    GetPlaylistSongsActivity provideGetPlaylistSongsActivity();
    CreatePlaylistActivity provideCreatePlaylistActivity();
}
