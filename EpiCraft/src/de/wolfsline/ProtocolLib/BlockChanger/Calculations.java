package de.wolfsline.ProtocolLib.BlockChanger;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.google.common.base.Stopwatch;

import de.wolfsline.helpClasses.changeID;

public class Calculations {
	// Used to pass around detailed information about chunks
	private static class ChunkInfo {
	    public int chunkX;
	    public int chunkZ;
	    public int chunkMask;
	    public int extraMask;
	    public int chunkSectionNumber;
	    public int extraSectionNumber;
	    public boolean hasContinous;
	    public byte[] data;
	    public Player player;
	    public int startIndex;
	    public int size;
	    public int blockSize;
	}
		
	// Useful Minecraft constants
	private static final int BYTES_PER_NIBBLE_PART = 2048;
	private static final int CHUNK_SEGMENTS = 16;
	private static final int NIBBLES_REQUIRED = 4;
	private static final int BIOME_ARRAY_LENGTH = 256;
	
	// Used to get a chunk's specific lookup table
	private EventScheduler scheduler;
	private ConversionCache cache;
	
	public HashMap<UUID, changeID> map = new HashMap<UUID, changeID>();
	
	public Calculations(ConversionCache cache, EventScheduler scheduler) {
		this.cache = cache;
		this.scheduler = scheduler;
	}
	
	public boolean isImportantChunkBulk(PacketContainer packet, Player player) throws FieldAccessException {
    	StructureModifier<int[]> intArrays = packet.getSpecificModifier(int[].class);
        int[] x = intArrays.read(0); 
        int[] z = intArrays.read(1); 
        
        for (int i = 0; i < x.length; i++) {
            if (Math.abs(x[i] - (((int) player.getLocation().getX()) >> 4)) == 0 && 
            	Math.abs(z[i] - (((int) player.getLocation().getZ())) >> 4) == 0) {
                return true;
            }
        }
        return false;
	}
	
	public boolean isImportantChunk(PacketContainer packet, Player player) throws FieldAccessException {
		StructureModifier<Integer> ints = packet.getSpecificModifier(int.class);
		int x = ints.read(0); 	
        int y = ints.read(1);
        
        if (Math.abs(x - (((int) player.getLocation().getX()) >> 4)) == 0 && 
        	Math.abs(y - (((int) player.getLocation().getZ())) >> 4) == 0) {
        	return true;
        }
        return false;
	}
	
	public void translateMapChunkBulk(PacketContainer packet, Player player) throws FieldAccessException {
    	StructureModifier<int[]> intArrays = packet.getSpecificModifier(int[].class);
    	StructureModifier<byte[]> byteArrays = packet.getSpecificModifier(byte[].class);
    	
        int[] x = intArrays.read(0); // getPrivateField(packet, "c");
        int[] z = intArrays.read(1); // getPrivateField(packet, "d");
    	
        ChunkInfo[] infos = new ChunkInfo[x.length];
        
        int dataStartIndex = 0;
        int[] chunkMask = intArrays.read(2); // packet.a;
        int[] extraMask = intArrays.read(3); // packet.b;
        
        for (int chunkNum = 0; chunkNum < infos.length; chunkNum++) {
            // Create an info objects
            ChunkInfo info = new ChunkInfo();
            infos[chunkNum] = info;
            info.player = player;
            info.chunkX = x[chunkNum];
            info.chunkZ = z[chunkNum];
            info.chunkMask = chunkMask[chunkNum];
            info.extraMask = extraMask[chunkNum];
            info.hasContinous = true; // Always true
            info.data = byteArrays.read(1); //packet.buildBuffer;
            
            // Check for Spigot
            if (info.data == null || info.data.length == 0) {
            	info.data = packet.getSpecificModifier(byte[][].class).read(0)[chunkNum];
            } else {
            	info.startIndex = dataStartIndex;
            }
            
            translateChunkInfoAndObfuscate(info, info.data);
            dataStartIndex += info.size;
        }
    }
	
    // Mimic the ?? operator in C#
    private <T> T getOrDefault(T value, T defaultIfNull) {
    	return value != null ? value : defaultIfNull;
    }
    
    public void translateMapChunk(PacketContainer packet, Player player) throws FieldAccessException  {
    	StructureModifier<Integer> ints = packet.getSpecificModifier(int.class);
    	StructureModifier<byte[]> byteArray = packet.getSpecificModifier(byte[].class);
        
        // Create an info objects
        ChunkInfo info = new ChunkInfo();
        info.player = player;
        info.chunkX = ints.read(0); 	// packet.a;
        info.chunkZ = ints.read(1); 	// packet.b;
        info.chunkMask = ints.read(2); 	// packet.c;
        info.extraMask = ints.read(3);  // packet.d;
        info.data = byteArray.read(1);  // packet.inflatedBuffer;
        info.hasContinous = getOrDefault(packet.getBooleans().readSafely(0), true);
        info.startIndex = 0;
        
        if (info.data != null) { 
        	translateChunkInfoAndObfuscate(info, info.data);
        }
    }
        
   
        
   
    
    private ChunkCoordInt getChunkCoordinate(PacketContainer packet) {
    	StructureModifier<Integer> ints = packet.getSpecificModifier(int.class);
    	
    	if (ints.size() >= 2) {
    		return new ChunkCoordInt(ints.read(0), ints.read(1));
    	} else {
    		// I forgot to add a wrapper - sorry
    		return ChunkCoordInt.fromHandle(packet.getModifier().read(0));
    	}
    }
    
   
    
   

    private boolean isChunkLoaded(World world, int x, int z) {
        return world.isChunkLoaded(x, z);
    }
    
    private void translateChunkInfoAndObfuscate(ChunkInfo info, byte[] returnData) {
        // Compute chunk number
        for (int i = 0; i < CHUNK_SEGMENTS; i++) {
            if ((info.chunkMask & (1 << i)) > 0) {
                info.chunkSectionNumber++;
            }
            if ((info.extraMask & (1 << i)) > 0) {
                info.extraSectionNumber++;
            }
        }
        
        // There's no sun/moon in the end or in the nether, so Minecraft doesn't sent any skylight information
        // This optimization was added in 1.4.6. Note that ideally you should get this from the "f" (skylight) field.
        int skylightCount = info.player.getWorld().getEnvironment() == Environment.NORMAL ? 1 : 0;
        
        // The total size of a chunk is the number of blocks sent (depends on the number of sections) multiplied by the 
        // amount of bytes per block. This last figure can be calculated by adding together all the data parts:
        //   For any block:
        //    * Block ID          -   8 bits per block (byte)
        //    * Block metadata    -   4 bits per block (nibble)
        //    * Block light array -   4 bits per block
        //   If 'worldProvider.skylight' is TRUE
        //    * Sky light array   -   4 bits per block
        //   If the segment has extra data:
        //    * Add array         -   4 bits per block
        //   Biome array - only if the entire chunk (has continous) is sent:
        //    * Biome array       -   256 bytes
        // 
        // A section has 16 * 16 * 16 = 4096 blocks. 
        info.size = BYTES_PER_NIBBLE_PART * ((NIBBLES_REQUIRED + skylightCount) * info.chunkSectionNumber + info.extraSectionNumber) + (info.hasContinous ? BIOME_ARRAY_LENGTH : 0);
        
        info.blockSize = 4096 * info.chunkSectionNumber;
        
        if (info.startIndex + info.blockSize > info.data.length) {
            return;
        }

        // Make sure the chunk is loaded 
        if (isChunkLoaded(info.player.getWorld(), info.chunkX, info.chunkZ)) {
        	// Invoke the event
        	SegmentLookup baseLookup = cache.getDefaultLookupTable();
        	SegmentLookup lookup = scheduler.getChunkConversion(baseLookup, info.player, info.chunkX, info.chunkZ);
        	
        	// Save the result to the cache, if it's not the default
        	if (!baseLookup.equals(lookup)) {
        		cache.saveCache(info.player, info.chunkX, info.chunkZ, lookup);
        	} else {
        		cache.saveCache(info.player, info.chunkX, info.chunkZ, null);
        	}
        	
            translate(lookup, info);
        }
    }
    
    private void translate(SegmentLookup lookup, ChunkInfo info) {
        // Loop over 16x16x16 chunks in the 16x256x16 column
        int idIndexModifier = 0;
        
        int idOffset = info.startIndex;
        int dataOffset = idOffset + info.chunkSectionNumber * 4096;
              
		//Stopwatch watch = new Stopwatch();
		//watch.start();
        
        for (int i = 0; i < 16; i++) {
            // If the bitmask indicates this chunk is sent
            if ((info.chunkMask & 1 << i) > 0) {
            	
            	ConversionLookup view = lookup.getSegmentView(i);
            	
                int relativeIDStart = idIndexModifier * 4096;
                int relativeDataStart = idIndexModifier * 2048;
                
                //boolean useExtraData = (info.extraMask & (1 << i)) > 0;
                int blockIndex = idOffset + relativeIDStart;
                int dataIndex = dataOffset + relativeDataStart;
 
                // Stores the extra value
                int output = 0;
                
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++)  {
                        	
                        	int blockID = info.data[blockIndex] & 0xFF;
                        	if(map.containsKey(info.player.getUniqueId())){
                        		changeID cID = map.get(info.player.getUniqueId());
                        		blockID = cID.getChangeID(blockID);
                        	}
                            // Transform block
                            info.data[blockIndex] = (byte) view.getBlockLookup(blockID);
                        	
							if ((blockIndex & 0x1) == 0) {
								int blockData = info.data[dataIndex] & 0xF;
								
								// Update the higher nibble
								output |= view.getDataLookup(blockID, blockData);
								
							} else {
								int blockData = (info.data[dataIndex] >> 4) & 0xF;
								
								// Update the lower nibble
								output |= view.getDataLookup(blockID, blockData) << 4; ;
								
								// Write the result
								info.data[dataIndex] = (byte) (output & 0xFF);
								output = 0;
								dataIndex++;
							}

                            blockIndex++;
                        }
                    }
                }
                
                idIndexModifier++;
            }
        }
        
        //watch.stop();
        //System.out.println(String.format("Processed x: %s, z: %s in %s ms.", 
        //			       info.chunkX, info.chunkZ, 
        //			       getMilliseconds(watch))
        //);
        
        // We're done
    }
    
    // For Minecraft 1.7.2
    private static class ChunkCoordInt {
    	private static FieldAccessor COORD_X;
    	private static FieldAccessor COORD_Z;
    	
    	public final int x;
    	public final int z;

    	/**
    	 * Construct a new chunk coordinate.
    	 * @param x - the x index of the chunk.
    	 * @param z - the z index of the chunk.
    	 */
    	public ChunkCoordInt(int x, int z) {
			this.x = x;
			this.z = z;
		}

    	/**
    	 * Retrieve a new chunk coord from an object handle.
    	 * @param handle - the handle.
    	 * @return The chunk coordinate.
    	 */
		public static ChunkCoordInt fromHandle(Object handle) {
    		if (COORD_X == null || COORD_Z == null) {
    			COORD_X = Accessors.getFieldAccessor(handle.getClass(), "x", true);
    			COORD_Z = Accessors.getFieldAccessor(handle.getClass(), "z", true);
    		}
    		
    		return new ChunkCoordInt(
				 (Integer) COORD_X.get(handle),
				 (Integer) COORD_Z.get(handle)
    		);
    	}
    }
    
	public static double getMilliseconds(Stopwatch watch) {
		return watch.elapsedTime(TimeUnit.NANOSECONDS) / 1000000.0;
	}
}