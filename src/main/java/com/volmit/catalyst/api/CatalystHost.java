package com.volmit.catalyst.api;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface CatalystHost extends PacketListener, Listener
{
	public Object packetTitleMessage(String title);

	public Object packetSubtitleMessage(String subtitle);

	public Object packetActionBarMessage(String actionmsg);

	public Object packetResetTitle();

	public Object packetClearTitle();

	public Object packetTimes(int in, int stay, int out);

	/**
	 * Get latest settings for the given player
	 *
	 * @param p
	 *            the player
	 * @return the player settings
	 */
	public PlayerSettings getSettings(Player p);

	/**
	 * Send a packet to the specified player
	 *
	 * @param p
	 *            the player
	 * @param o
	 *            the packet
	 */
	public void sendPacket(Player p, Object o);

	/**
	 * Send a packet to all players within a radius from the specified location
	 *
	 * @param radius
	 *            the radius
	 * @param l
	 *            the location
	 * @param o
	 *            the packet
	 */
	public void sendRangedPacket(double radius, Location l, Object o);

	/**
	 * Send a packet to all players in the specified world
	 *
	 * @param w
	 *            the world
	 * @param o
	 *            the packet
	 */
	public void sendGlobalPacket(World w, Object o);

	/**
	 * Send a packet to all players on the server
	 *
	 * @param o
	 *            the packet
	 */
	public void sendUniversalPacket(Object o);

	/**
	 * Send a packet to all players which have a view distance (captured from client
	 * settings) that can "observe" the specified chunk. This is very useful for
	 * avoiding the player's empty chunk cache, when setting a block outside of
	 * their view distance, or in an unloaded chunk.
	 *
	 * @param c
	 *            the chunk
	 * @param o
	 *            the packet
	 */
	public void sendViewDistancedPacket(Chunk c, Object o);

	/**
	 * Checks if the specified player can see the given chunk
	 *
	 * @param c
	 *            the chunk
	 * @param p
	 *            the player
	 * @return true if the player can
	 */
	public boolean canSee(Chunk c, Player p);

	/**
	 * Checks if the specified player can see the chunk of the given location
	 *
	 * @param l
	 *            the location
	 * @param p
	 *            the player
	 * @return true if the player can
	 */
	public boolean canSee(Location l, Player p);

	/**
	 * Returns the view distance of the given player (maxed by the server's view
	 * distance)
	 *
	 * @param p
	 *            the player
	 * @return the view distance sent from the player client
	 */
	public int getViewDistance(Player p);

	/**
	 * List all of the players that has the specified chunk in their visible view
	 * distnace
	 *
	 * @param c
	 *            the chunk
	 * @return the players
	 */
	public List<Player> getObservers(Chunk c);

	/**
	 * List all of the players that has the specified location's chunk in their
	 * visible view distnace
	 *
	 * @param c
	 *            the chunk
	 * @return the players
	 */
	public List<Player> getObservers(Location l);

	/**
	 * Get the server NMS package version e.g 1_12_R1
	 *
	 * @return the version
	 */
	public String getServerVersion();

	/**
	 * Get a friendly readable version indicator for the intended server/client
	 * version such as 1.12.X
	 *
	 * @return the version
	 */
	public String getVersion();

	/**
	 * Start the handler
	 */
	public void start();

	/**
	 * Stop the handler
	 */
	public void stop();
}
