package de.wolfsline.ProtocolLib.BlockChanger;

import java.io.Serializable;
import java.util.Arrays;

import com.google.common.base.Objects;

/**
 * Represents multiple lookup tables for each segment in a chunk.
 * <p>
 * Top level write operations are performed on every underlying segment. 
 * <p>
 * Read operations are performed on the lookup table that is shared
 * by most segments. The lowest y-position is used as a tie breaker.
 * <p>
 * Note that this class is not thread safe.
 * @author Kristian
 */
public class ChunkSegmentLookup implements SegmentLookup, Serializable {

	/**
	 * Generated by Eclipse.
	 */
	private static final long serialVersionUID = -5088539705779816832L;

	private final ConversionLookup[] segments;
	private transient LazyCopyLookup[] lazyCopies;

	private final ConversionLookup baseLookup;
	private final int segmentCount;

	/**
	 * Number of segments that uses the base lookup table.
	 */
	private int baseUseCount;

	public ChunkSegmentLookup(ConversionLookup baseLookup) {
		this(baseLookup, SegmentLookup.MINECRAFT_SEGMENT_COUNT);
	}

	public ChunkSegmentLookup(ConversionLookup baseLookup, int segmentCount) {
		this.baseLookup = baseLookup;
		this.segments = new ConversionLookup[segmentCount];
		this.segmentCount = segmentCount;
		this.baseUseCount = segmentCount;
	}

	private ChunkSegmentLookup(ChunkSegmentLookup other) {
		this.baseLookup = new LazyCopyLookup(other.baseLookup);
		this.segments = new ConversionLookup[other.segmentCount];
		this.lazyCopies = new LazyCopyLookup[other.segmentCount];
		this.segmentCount = other.segmentCount;
		this.baseUseCount = other.baseUseCount;

		// Lazy copy of every segment
		for (int y = 0; y < segmentCount; y++) {
			if (other.segments[y] != null) {
				this.lazyCopies[y] = new LazyCopyLookup(other.segments[y]);
			}
		}
	}

	@Override
	public ConversionLookup getSegmentView(int chunkY) {
		if (chunkY < 0 || chunkY >= segmentCount)
			throw new IllegalArgumentException("y (" + chunkY + ") must be in the range 0 - " + (segmentCount - 1));

		ConversionLookup lookup = segments[chunkY];
		final int index = chunkY;

		// We need to create a wrapper than automatically creates a new lookup table whenever it is modified
		// This new lookup table should be used for all subsequent reads and writes on this segment.
		if (lookup == null) {
			// It's a transient field, so we may need to create it here
			if (lazyCopies == null) {
				lazyCopies = new LazyCopyLookup[segmentCount];
			}

			// See if it is set to anything
			if (lazyCopies[chunkY] == null) {
				LazyCopyLookup copy = new LazyCopyLookup(baseLookup) {
					@Override
					protected void checkModifications() {
						super.checkModifications();
						segments[index] = delegate;
						lazyCopies[index] = null;
						baseUseCount--;
					}
				};

				// Temporary, until the caller has modified the lookup table
				return lazyCopies[chunkY] = copy;

			} else {
				// Keep on returning that lazy copy
				return lazyCopies[chunkY];
			}
		} 

		// Use the lookup table at the given location
		return lookup;
	}

	/**
	 * Retrieve the lookup table that is used most.
	 * @return Most used lookup table.
	 */
	private ConversionLookup getCommonLookup() {
		if (baseUseCount > 1)
			return baseLookup;
		else 
			return getSegmentView(0);
	}

	@Override
	public int getSegmentCount() {
		return segments.length;
	}

	@Override
	public void setBlockLookup(int blockID, int newBlockID, int chunkY) {
		getSegmentView(chunkY).setBlockLookup(blockID, newBlockID);
	}

	@Override
	public int getBlockLookup(int blockID, int chunkY) {
		return getSegmentView(chunkY).getBlockLookup(blockID);
	}

	@Override
	public void setDataLookup(int blockID, int originalDataValue, int newDataValue, int chunkY) {
		getSegmentView(chunkY).setDataLookup(blockID, originalDataValue, newDataValue);
	}

	@Override
	public int getDataLookup(int blockID, int dataValue, int chunkY) {
		return getSegmentView(chunkY).getDataLookup(blockID, dataValue);
	}

	@Override
	public byte[] getDataLookup() {
		return getCommonLookup().getDataLookup();
	}

	@Override
	public byte[] getBlockLookup() {
		return getCommonLookup().getBlockLookup();
	}

	@Override
	public int getBlockLookup(int blockID) {
		return getCommonLookup().getBlockLookup(blockID);
	}

	@Override
	public int getDataLookup(int blockID, int dataValue) {
		return getCommonLookup().getDataLookup(blockID, dataValue);
	}

	@Override
	public void setBlockLookup(int blockID, int newBlockID) {
		for (int y = 0; y < segmentCount; y++) {
			if (segments[y] != null) {
				segments[y].setBlockLookup(blockID, newBlockID);
			}
		}

		// Modify the base lookup too
		if (baseUseCount > 0) {
			baseLookup.setBlockLookup(blockID, newBlockID);
		}
	}

	@Override
	public void setDataLookup(int blockID, int originalDataValue, int newDataValue) {
		for (int y = 0; y < segmentCount; y++) {
			if (segments[y] != null) {
				segments[y].setDataLookup(blockID, originalDataValue, newDataValue);
			}
		}

		if (baseUseCount > 0) {
			baseLookup.setDataLookup(blockID, originalDataValue, newDataValue);
		}
	}

	@Override
	public boolean equals(Object other) {
	    if(other == this) return true;
	    if(other == null) return false;

	    // Check the actual content
	    if (other instanceof ChunkSegmentLookup) {
	    	final ChunkSegmentLookup segmentLookup = (ChunkSegmentLookup) other;
	    	return Arrays.deepEquals(segments, segmentLookup.segments) &&
	    		   Objects.equal(baseLookup, segmentLookup.baseLookup);
	    }
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(Arrays.hashCode(segments), baseLookup);
	}

	@Override
	public ConversionLookup deepClone() {
		return new ChunkSegmentLookup(this);
	}
}