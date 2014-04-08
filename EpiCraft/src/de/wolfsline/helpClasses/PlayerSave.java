package de.wolfsline.helpClasses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerSave{
	
	private float xp;
	private int damage, level;
	private List<MyItemStack> list;
	private MyItemStack boots, leggings, chestplate, helmet;
	private Collection<PotionEffect> potionEffects;
	
	public PlayerSave(){
		this.xp = 0.0f;
		this.damage = 20;
		this.list = new ArrayList<MyItemStack>();
		this.boots = null;
		this.leggings = null;
		this.chestplate = null;
		this.helmet = null;
	}
	
	public void saveInventory(Player p){
		Inventory inv = p.getInventory();
		for( int i = 0 ; i < inv.getSize() ; i++ ){
            ItemStack stack = inv.getItem(i);
            if(stack == null)
            	list.add(null);
            else
            	list.add(new MyItemStack(stack));
		}
		this.damage = (int) p.getHealth();
		this.helmet = new MyItemStack(p.getInventory().getHelmet());
		this.leggings = new MyItemStack(p.getInventory().getLeggings());
		this.chestplate = new MyItemStack(p.getInventory().getChestplate());
		this.boots = new MyItemStack(p.getInventory().getBoots());
		this.xp = p.getExp();
		this.level = p.getLevel();
		this.potionEffects = p.getActivePotionEffects();
		p.getInventory().clear();
		p.updateInventory();
		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		//Helm
		ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
		helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		helmet.addEnchantment(Enchantment.WATER_WORKER, 1);
		helmet.addEnchantment(Enchantment.DURABILITY, 3);
		helmet.addEnchantment(Enchantment.OXYGEN, 3);
		//Brustpanzer
		ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
		chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		chestplate.addEnchantment(Enchantment.DURABILITY, 3);
		//Hose
		ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
		leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		leggings.addEnchantment(Enchantment.DURABILITY, 3);
		//Schuhe
		ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
		boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		boots.addEnchantment(Enchantment.DURABILITY, 3);
		boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
		//Slot 1 Schwert
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		sword.addEnchantment(Enchantment.DURABILITY, 3);
		//Slot 2 Spitzhacke
		ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
		pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5);
		pickaxe.addEnchantment(Enchantment.DURABILITY, 3);
		//Slot 3 Schaufel
		ItemStack spade = new ItemStack(Material.DIAMOND_SPADE);
		spade.addEnchantment(Enchantment.DIG_SPEED, 5);
		spade.addEnchantment(Enchantment.DURABILITY, 3);
		//Slot 4 Axt
		ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
		axe.addEnchantment(Enchantment.DIG_SPEED, 5);
		axe.addEnchantment(Enchantment.DURABILITY, 3);
		//Slot 5 Lava
		ItemStack lava = new ItemStack(Material.LAVA, 64);
		//Slot 5 Wasser
		ItemStack water = new ItemStack(Material.WATER, 64);
		//Slot 6 Feuerzeug
		ItemStack flindAndStell = new ItemStack(Material.FLINT_AND_STEEL);
		p.getInventory().addItem(sword);
		p.getInventory().addItem(pickaxe);
		p.getInventory().addItem(spade);
		p.getInventory().addItem(axe);
		p.getInventory().addItem(lava);
		p.getInventory().addItem(water);
		p.getInventory().addItem(flindAndStell);
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chestplate);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 1));
	}
	
	public void restoreInventory(Player p){
		for(PotionEffect effect : p.getActivePotionEffects()){
			p.removePotionEffect(effect.getType());
		}
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		Inventory inv = p.getInventory();
		for( int i = 0 ; i < inv.getSize() ; i++ ){
            MyItemStack stack = list.get(i);
            if(stack == null)
            	continue;
            inv.setItem(i, stack.toItemStack());
		}
		if(this.helmet != null)
			p.getInventory().setHelmet(this.helmet.toItemStack());
		if(this.chestplate != null)
			p.getInventory().setChestplate(this.chestplate.toItemStack());
		if(this.leggings != null)
			p.getInventory().setLeggings(this.leggings.toItemStack());
		if(this.boots != null)
			p.getInventory().setBoots(this.boots.toItemStack());
		p.setHealth(this.damage);
		p.setExp(this.xp);
		p.setLevel(this.level);
		p.addPotionEffects(this.potionEffects);
		list.clear();
	}
}
