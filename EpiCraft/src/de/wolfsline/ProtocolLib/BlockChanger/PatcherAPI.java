package de.wolfsline.ProtocolLib.BlockChanger;

import org.bukkit.entity.Player;

/**
 * Able to automatically translate every block and item on the server to a different type. 
 * <p>
 * This conversion is only client side and will never affect the actual world files. To use
 * different conversions per player or chunk, subscribe to the ChunkPostProcessingEvent.
 * <p>
 * To convert items, subscribe to the event ItemConvertingEvent.
 * @author Kristian
 */
public final class PatcherAPI extends ChunkSegmentLookup {
	/**
	 * Generated by Eclipse.
	 */
	private static final long serialVersionUID = 9149758527563036643L;

	public PatcherAPI() {
		// Use the identity lookup table
		super(new ChunkLookup());
	}

	/**
	 * Re-transmit the given chunk to the given player.
	 * @param player - the given player.
	 * @param chunkX - the chunk x coordinate.
	 * @param chunkZ - the chunk z coordinate.
	 */
	public void resendChunk(Player player, int chunkX, int chunkZ) {
		ChunkUtility.resendChunk(player, chunkX, chunkZ);
	}

	/**
	 * Generate a translation table that doesn't change any value.
	 * @param max - the maximum number of entries in the table.
	 * @return The identity translation table.
	 */
	public byte[] getIdentityTranslation(int max) {
		return ChunkLookup.getIdentityTranslation(max);
	}

	/**
	 * Generate an identity translation table that doesn't change anything.
	 * @param blocks - number of blocks.
	 * @param entries - number of data values per block.
	 * @return The translation table.
	 */
	public byte[] getDataIdentity(int blocks, int entries) {
		return ChunkLookup.getDataIdentity(blocks, entries);
	}
}