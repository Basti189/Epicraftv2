package de.wolfsline.LogBlock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class Utils {

	private static final Set<Set<Integer>> blockEquivalents;
	private static final Set<Material> relativeBreakable;
	private static final Set<Material> relativeTopBreakable;
	private static final Set<Material> relativeTopFallables;
	private static final Set<Material> fallingEntityKillers;

	private static final Set<Material> cropBlocks;
	private static final Set<Material> containerBlocks;

	static {
		blockEquivalents = new HashSet<Set<Integer>>(7);
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(2, 3, 60)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(8, 9, 79)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(10, 11)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(61, 62)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(73, 74)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(75, 76)));
		blockEquivalents.add(new HashSet<Integer>(Arrays.asList(93, 94)));

		// Blocks that break when they are attached to a block
		relativeBreakable = new HashSet<Material>(11);
		relativeBreakable.add(Material.WALL_SIGN);
		relativeBreakable.add(Material.LADDER);
		relativeBreakable.add(Material.STONE_BUTTON);
		relativeBreakable.add(Material.WOOD_BUTTON);
		relativeBreakable.add(Material.REDSTONE_TORCH_ON);
		relativeBreakable.add(Material.REDSTONE_TORCH_OFF);
		relativeBreakable.add(Material.LEVER);
		relativeBreakable.add(Material.TORCH);
		relativeBreakable.add(Material.TRAP_DOOR);
		relativeBreakable.add(Material.TRIPWIRE_HOOK);
		relativeBreakable.add(Material.COCOA);

		// Blocks that break when they are on top of a block
		relativeTopBreakable = new HashSet<Material>(33);
		relativeTopBreakable.add(Material.SAPLING);
		relativeTopBreakable.add(Material.LONG_GRASS);
		relativeTopBreakable.add(Material.DEAD_BUSH);
		relativeTopBreakable.add(Material.YELLOW_FLOWER);
		relativeTopBreakable.add(Material.RED_ROSE);
		relativeTopBreakable.add(Material.BROWN_MUSHROOM);
		relativeTopBreakable.add(Material.RED_MUSHROOM);
		relativeTopBreakable.add(Material.CROPS);
		relativeTopBreakable.add(Material.POTATO);
		relativeTopBreakable.add(Material.CARROT);
		relativeTopBreakable.add(Material.WATER_LILY);
		relativeTopBreakable.add(Material.CACTUS);
		relativeTopBreakable.add(Material.SUGAR_CANE_BLOCK);
		relativeTopBreakable.add(Material.FLOWER_POT);
		relativeTopBreakable.add(Material.POWERED_RAIL);
		relativeTopBreakable.add(Material.DETECTOR_RAIL);
		relativeTopBreakable.add(Material.ACTIVATOR_RAIL);
		relativeTopBreakable.add(Material.RAILS);
		relativeTopBreakable.add(Material.REDSTONE_WIRE);
		relativeTopBreakable.add(Material.SIGN_POST);
		relativeTopBreakable.add(Material.STONE_PLATE);
		relativeTopBreakable.add(Material.WOOD_PLATE);
		relativeTopBreakable.add(Material.IRON_PLATE);
		relativeTopBreakable.add(Material.GOLD_PLATE);
		relativeTopBreakable.add(Material.SNOW);
		relativeTopBreakable.add(Material.DIODE_BLOCK_ON);
		relativeTopBreakable.add(Material.DIODE_BLOCK_OFF);
		relativeTopBreakable.add(Material.REDSTONE_COMPARATOR_ON);
		relativeTopBreakable.add(Material.REDSTONE_COMPARATOR_OFF);
		relativeTopBreakable.add(Material.WOODEN_DOOR);
		relativeTopBreakable.add(Material.IRON_DOOR_BLOCK);
		relativeTopBreakable.add(Material.CARPET);
		relativeTopBreakable.add(Material.DOUBLE_PLANT);

		// Blocks that fall
		relativeTopFallables = new HashSet<Material>(4);
		relativeTopFallables.add(Material.SAND);
		relativeTopFallables.add(Material.GRAVEL);
		relativeTopFallables.add(Material.DRAGON_EGG);
		relativeTopFallables.add(Material.ANVIL);

		// Blocks that break falling entities
		fallingEntityKillers = new HashSet<Material>(32);
		fallingEntityKillers.add(Material.SIGN_POST);
		fallingEntityKillers.add(Material.WALL_SIGN);
		fallingEntityKillers.add(Material.STONE_PLATE);
		fallingEntityKillers.add(Material.WOOD_PLATE);
		fallingEntityKillers.add(Material.IRON_PLATE);
		fallingEntityKillers.add(Material.GOLD_PLATE);
		fallingEntityKillers.add(Material.SAPLING);
		fallingEntityKillers.add(Material.YELLOW_FLOWER);
		fallingEntityKillers.add(Material.RED_ROSE);
		fallingEntityKillers.add(Material.CROPS);
		fallingEntityKillers.add(Material.CARROT);
		fallingEntityKillers.add(Material.POTATO);
		fallingEntityKillers.add(Material.RED_MUSHROOM);
		fallingEntityKillers.add(Material.BROWN_MUSHROOM);
		fallingEntityKillers.add(Material.STEP);
		fallingEntityKillers.add(Material.WOOD_STEP);
		fallingEntityKillers.add(Material.TORCH);
		fallingEntityKillers.add(Material.FLOWER_POT);
		fallingEntityKillers.add(Material.POWERED_RAIL);
		fallingEntityKillers.add(Material.DETECTOR_RAIL);
		fallingEntityKillers.add(Material.ACTIVATOR_RAIL);
		fallingEntityKillers.add(Material.RAILS);
		fallingEntityKillers.add(Material.LEVER);
		fallingEntityKillers.add(Material.REDSTONE_WIRE);
		fallingEntityKillers.add(Material.REDSTONE_TORCH_ON);
		fallingEntityKillers.add(Material.REDSTONE_TORCH_OFF);
		fallingEntityKillers.add(Material.DIODE_BLOCK_ON);
		fallingEntityKillers.add(Material.DIODE_BLOCK_OFF);
		fallingEntityKillers.add(Material.REDSTONE_COMPARATOR_ON);
		fallingEntityKillers.add(Material.REDSTONE_COMPARATOR_OFF);
		fallingEntityKillers.add(Material.DAYLIGHT_DETECTOR);
		fallingEntityKillers.add(Material.CARPET);

		// Crop Blocks
		cropBlocks = new HashSet<Material>(5);
		cropBlocks.add(Material.CROPS);
		cropBlocks.add(Material.MELON_STEM);
		cropBlocks.add(Material.PUMPKIN_STEM);
		cropBlocks.add(Material.CARROT);
		cropBlocks.add(Material.POTATO);

		// Container Blocks
		containerBlocks = new HashSet<Material>(6);
		containerBlocks.add(Material.CHEST);
		containerBlocks.add(Material.TRAPPED_CHEST);
		containerBlocks.add(Material.DISPENSER);
		containerBlocks.add(Material.DROPPER);
		containerBlocks.add(Material.HOPPER);
		containerBlocks.add(Material.BREWING_STAND);
		containerBlocks.add(Material.FURNACE);
		containerBlocks.add(Material.BURNING_FURNACE);
		containerBlocks.add(Material.BEACON);
		// Doesn't actually have a block inventory
		// containerBlocks.add(Material.ENDER_CHEST);
	}
	
	private static final BlockFace[] relativeBlockFaces = new BlockFace[] {
		BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN
	};
	
	public static boolean isTop(Material mat, byte data) {

		switch (mat) {
			case DOUBLE_PLANT:
				return data > 5;
			case IRON_DOOR_BLOCK:
			case WOODEN_DOOR:
				return data == 8 || data == 9;
			default:
				return false;
		}
	};
	
	public static Set<Set<Integer>> getBlockEquivalents() {
		return blockEquivalents;
	}

	public static Set<Material> getRelativeBreakables() {
		return relativeBreakable;
	}

	public static Set<Material> getRelativeTopBreakabls() {
		return relativeTopBreakable;
	}

	public static Set<Material> getRelativeTopFallables() {
		return relativeTopFallables;
	}

	public static Set<Material> getFallingEntityKillers() {
		return fallingEntityKillers;
	}

	public static Set<Material> getCropBlocks() {
		return cropBlocks;
	}

	public static Set<Material> getContainerBlocks() {
		return containerBlocks;
	}
	
}
