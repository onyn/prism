package me.botsko.prism.actionlibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.botsko.prism.Prism;
import me.botsko.prism.PrismConfig;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.players.PlayerIdentification;
import me.botsko.prism.players.PrismPlayer;

public class RecordingTask implements Runnable {

    /**
	 *
	 */
    private final Prism plugin;

    /**
     *
     * @param plugin
     */
    public RecordingTask(Prism plugin) {
        this.plugin = plugin;
    }

    /**
	 *
	 */
    public void save() {
        if( !RecordingQueue.getQueue().isEmpty() ) {
            insertActionsIntoDatabase();
        }
    }

    /**
     *
     * @param a
     */
    public static int insertActionIntoDatabase(Handler a) {
        String prefix = Prism.config.getString("prism.mysql.prefix");
        int id = 0;
        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement(
                "INSERT INTO " + prefix + "data (epoch,action_id,player_id,world_id,block_id,block_subid,old_block_id,old_block_subid,x,y,z) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
            )
        ) {

            // prepare to save to the db
            a.save();

            int world_id = 0;
            if( Prism.prismWorlds.containsKey( a.getWorldName() ) ) {
                world_id = Prism.prismWorlds.get( a.getWorldName() );
            }

            int action_id = 0;
            if( Prism.prismActions.containsKey( a.getType().getName() ) ) {
                action_id = Prism.prismActions.get( a.getType().getName() );
            }

            int player_id = 0;
            PrismPlayer prismPlayer = PlayerIdentification.cachePrismPlayer( a.getPlayerName() );
            if( prismPlayer != null ){
                player_id = prismPlayer.getId();
            }

            if( world_id == 0 || action_id == 0 || player_id == 0 ) {
                // @todo do something, error here
            }

            s.setLong( 1, System.currentTimeMillis() / 1000L );
            s.setInt( 2, world_id );
            s.setInt( 3, player_id );
            s.setInt( 4, world_id );
            s.setInt( 5, a.getBlockId() );
            s.setInt( 6, a.getBlockSubId() );
            s.setInt( 7, a.getOldBlockId() );
            s.setInt( 8, a.getOldBlockSubId() );
            s.setInt( 9, (int) a.getX() );
            s.setInt( 10, (int) a.getY() );
            s.setInt( 11, (int) a.getZ() );
            s.executeUpdate();

            try (ResultSet generatedKeys = s.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                }
            }

            // Add insert query for extra data if needed
            if( a.getData() != null && !a.getData().isEmpty() ) {
                try (PreparedStatement s2 = conn.prepareStatement("INSERT INTO " + prefix + "data_extra (data_id,data) VALUES (?,?)")) {
                    s2.setInt(1, id);
                    s2.setString(2, a.getData());
                    s2.executeUpdate();
                }
            }

        } catch ( final SQLException e ) {
            e.printStackTrace();
            // plugin.handleDatabaseException( e );
        }
        return id;
    }

    /**
     *
     * @throws SQLException
     */
    public void insertActionsIntoDatabase() {
        if( RecordingQueue.getQueue().isEmpty() ) {
            return;
        }

        String prefix = plugin.getConfig().getString("prism.mysql.prefix");
        String query = "INSERT INTO " + prefix + "data (epoch,action_id,player_id,world_id,block_id,block_subid,old_block_id,old_block_subid,x,y,z) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        int actionsRecorded = 0;
        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
        ) {
            RecordingManager.failedDbConnectionCount = 0;

            int perBatch = plugin.getConfig().getInt( "prism.database.actions-per-insert-batch" );
            if( perBatch < 1 )
                perBatch = 1000;

            Prism.debug( "Beginning batch insert from queue. " + System.currentTimeMillis() );

            final ArrayList<Handler> extraDataQueue = new ArrayList<Handler>();

            int i = 0;
            while ( !RecordingQueue.getQueue().isEmpty() ) {

                if( conn.isClosed() ) {
                    Prism.log( "Prism database error. We have to bail in the middle of building primary bulk insert query." );
                    break;
                }

                final Handler a = RecordingQueue.getQueue().poll();

                // poll() returns null if queue is empty
                if( a == null )
                    break;

                int world_id = 0;
                if( Prism.prismWorlds.containsKey( a.getWorldName() ) ) {
                    world_id = Prism.prismWorlds.get( a.getWorldName() );
                }

                int action_id = 0;
                if( Prism.prismActions.containsKey( a.getType().getName() ) ) {
                    action_id = Prism.prismActions.get( a.getType().getName() );
                }

                int player_id = 0;
                PrismPlayer prismPlayer = PlayerIdentification.cachePrismPlayer( a.getPlayerName() );
                if( prismPlayer != null ){
                    player_id = prismPlayer.getId();
                }

                if( world_id == 0 || action_id == 0 || player_id == 0 ) {
                    // @todo do something, error here
                    Prism.log( "Cache data was empty. Please report to developer: world_id:" + world_id + "/"
                            + a.getWorldName() + " action_id:" + action_id + "/" + a.getType().getName()
                            + " player_id:" + player_id + "/" + a.getPlayerName() );
                    Prism.log( "HOWEVER, this likely means you have a broken prism database installation." );
                    continue;
                }

                if( a.isCanceled() )
                    continue;

                actionsRecorded++;

                s.setLong( 1, System.currentTimeMillis() / 1000L );
                s.setInt( 2, action_id );
                s.setInt( 3, player_id );
                s.setInt( 4, world_id );
                s.setInt( 5, a.getBlockId() );
                s.setInt( 6, a.getBlockSubId() );
                s.setInt( 7, a.getOldBlockId() );
                s.setInt( 8, a.getOldBlockSubId() );
                s.setInt( 9, (int) a.getX() );
                s.setInt( 10, (int) a.getY() );
                s.setInt( 11, (int) a.getZ() );
                s.addBatch();

                extraDataQueue.add( a );

                // Break out of the loop and just commit what we have
                if( i >= perBatch ) {
                    Prism.debug( "Recorder: Batch max exceeded, running insert. Queue remaining: "
                            + RecordingQueue.getQueue().size() );
                    break;
                }
                i++;
            }

            s.executeBatch();

            if( conn.isClosed() ) {
                Prism.log( "Prism database error. We have to bail in the middle of building primary bulk insert query." );
            } else {
                Prism.debug( "Batch insert was commit: " + System.currentTimeMillis() );
            }

            // Save the current count to the queue for short historical data
            plugin.queueStats.addRunCount( actionsRecorded );

            // Insert extra data
            try (ResultSet rs = s.getGeneratedKeys()) {
                insertExtraData( extraDataQueue, rs );
            }

        } catch ( SQLException e ) {
            RecordingManager.failedDbConnectionCount++;
            if( RecordingManager.failedDbConnectionCount > plugin.getConfig().getInt(
                "prism.database.max-failures-before-wait" ) ) {
                Prism.log( "Too many problems connecting. Giving up for a bit." );
                scheduleNextRecording();
            }
            plugin.handleDatabaseException( e );
        }
    }

    /**
     *
     * @param keys
     * @throws SQLException
     */
    protected void insertExtraData(ArrayList<Handler> extraDataQueue, ResultSet keys) throws SQLException {
        String prefix = plugin.getConfig().getString("prism.mysql.prefix");

        if( extraDataQueue.isEmpty() )
            return;

        try (
            Connection conn = Prism.dbc();
            PreparedStatement s = conn.prepareStatement("INSERT INTO " + prefix + "data_extra (data_id,data) VALUES (?,?)")
        ) {
            int i = 0;
            while ( keys.next() ) {

                if( conn.isClosed() ) {
                    Prism.log( "Prism database error. We have to bail in the middle of building bulk insert extra data query." );
                    break;
                }

                // @todo should not happen
                if( i >= extraDataQueue.size() ) {
                    Prism.log( "Skipping extra data for " + prefix + "data.id " + keys.getInt( 1 )
                            + " because the queue doesn't have data for it." );
                    continue;
                }

                final Handler a = extraDataQueue.get( i );

                if( a.getData() != null && !a.getData().isEmpty() ) {
                    s.setInt( 1, keys.getInt( 1 ) );
                    s.setString( 2, a.getData() );
                    s.addBatch();
                }

                i++;

            }
            s.executeBatch();

            if( conn.isClosed() ) {
                Prism.log( "Prism database error. We have to bail in the middle of building extra data bulk insert query." );
            }

        } catch ( final SQLException e ) {
            Prism.log( "Prism database error. Skipping extra data queue insertion." );
            plugin.handleDatabaseException( e );
        }
    }

    /**
	 *
	 */
    @Override
    public void run() {
        if( RecordingManager.failedDbConnectionCount > 5 ) {
            plugin.rebuildPool(); // force rebuild pool after several failures
        }
        save();
        scheduleNextRecording();
    }

    /**
     *
     * @return
     */
    protected int getTickDelayForNextBatch() {

        // If we have too many rejected connections, increase the schedule
        if( RecordingManager.failedDbConnectionCount > plugin.getConfig().getInt(
                "prism.database.max-failures-before-wait" ) ) { return RecordingManager.failedDbConnectionCount * 20; }

        int recorder_tick_delay = plugin.getConfig().getInt( "prism.queue-empty-tick-delay" );
        if( recorder_tick_delay < 1 ) {
            recorder_tick_delay = 3;
        }
        return recorder_tick_delay;
    }

    /**
	 *
	 */
    protected void scheduleNextRecording() {
        if( !plugin.isEnabled() ) {
            Prism.log( "Can't schedule new recording tasks as plugin is now disabled. If you're shutting down the server, ignore me." );
            return;
        }
        plugin.recordingTask = plugin.getServer().getScheduler()
                .runTaskLaterAsynchronously( plugin, new RecordingTask( plugin ), getTickDelayForNextBatch() );
    }
}
