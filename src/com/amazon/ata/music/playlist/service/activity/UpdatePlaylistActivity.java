package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.aws.dynamodb.DynamoDbClientProvider;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeChangeException;
import com.amazon.ata.music.playlist.service.exceptions.InvalidAttributeException;
import com.amazon.ata.music.playlist.service.models.PlaylistModel;
import com.amazon.ata.music.playlist.service.models.requests.UpdatePlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.UpdatePlaylistResult;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;

import com.amazon.ata.music.playlist.service.util.MusicPlaylistServiceUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Implementation of the UpdatePlaylistActivity for the MusicPlaylistService's UpdatePlaylist API.
 *
 * This API allows the customer to update their saved playlist's information.
 */
public class UpdatePlaylistActivity implements RequestHandler<UpdatePlaylistRequest, UpdatePlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    public UpdatePlaylistActivity(){playlistDao = new PlaylistDao(new DynamoDBMapper(DynamoDbClientProvider.getDynamoDBClient(Regions.US_WEST_1)));}

    /**
     * Instantiates a new UpdatePlaylistActivity object.
     *
     * @param playlistDao PlaylistDao to access the playlist table.
     */
    @Inject
    public UpdatePlaylistActivity(PlaylistDao playlistDao) {
        this.playlistDao = playlistDao;
    }

    /**
     * This method handles the incoming request by retrieving the playlist, updating it,
     * and persisting the playlist.
     * <p>
     * It then returns the updated playlist.
     * <p>
     * If the playlist does not exist, this should throw a PlaylistNotFoundException.
     * <p>
     * If the provided playlist name or customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     * <p>
     * If the request tries to update the customer ID,
     * this should throw an InvalidAttributeChangeException
     *
     * @param updatePlaylistRequest request object containing the playlist ID, playlist name, and customer ID
     *                              associated with it
     * @return updatePlaylistResult result object containing the API defined {@link PlaylistModel}
     */
    @Override
    public UpdatePlaylistResult handleRequest(final UpdatePlaylistRequest updatePlaylistRequest, Context context) {
        log.info("Received UpdatePlaylistRequest {}", updatePlaylistRequest);
        String requestedId = updatePlaylistRequest.getId();
        Playlist playlist = playlistDao.getPlaylist(requestedId);
        PlaylistModel model = new PlaylistModel();
        if(!MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getCustomerId())){
            throw new InvalidAttributeException("invalid customer ID provided");
        }
        if(!MusicPlaylistServiceUtils.isValidString(updatePlaylistRequest.getName())){
            throw new InvalidAttributeException("invalid playlist name provided");
        }
        if(!Objects.equals(playlist.getCustomerId(), updatePlaylistRequest.getCustomerId())){
            throw new InvalidAttributeChangeException("this playlist isn't yours");
        }else{
            Playlist playlistToSave = new Playlist();
            playlistToSave.setId(playlist.getId());
            playlistToSave.setCustomerId(updatePlaylistRequest.getCustomerId());
            playlistToSave.setName(updatePlaylistRequest.getName());
            playlistToSave.setSongCount(playlist.getSongCount());
            playlistToSave.setTags(playlist.getTags());
            playlistToSave.setSongList(playlist.getSongList());
            playlistDao.savePlaylist(playlistToSave);
            ModelConverter converter = new ModelConverter();
            model = converter.toPlaylistModel(playlistToSave);
        }
        return UpdatePlaylistResult.builder()
                .withPlaylist(model)
                .build();
    }
}
