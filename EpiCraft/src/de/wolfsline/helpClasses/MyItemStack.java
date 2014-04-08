package de.wolfsline.helpClasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class MyItemStack {
	
	public int amount, typeID;
    public byte data;
    public short durability;
    public String displayName;
    public Map<Enchantment, Integer> enchantmentList = new HashMap<Enchantment, Integer>();
    public List<String> lore;
    private boolean isNULL = false;
   
    public MyItemStack(ItemStack itemStack) {
    	if(itemStack == null){
    		isNULL = true;
    		return;
    	}
        amount = itemStack.getAmount();
        durability = itemStack.getDurability();
        enchantmentList = itemStack.getEnchantments();
        typeID = itemStack.getTypeId();
        data = itemStack.getData().getData();
        displayName = itemStack.getItemMeta().getDisplayName();
        lore = itemStack.getItemMeta().getLore();
    }
   
    public ItemStack toItemStack() {
    	if(isNULL)
    		return null;
        ItemStack newStack = new ItemStack(typeID, amount);
        newStack.setData(new MaterialData(typeID, data));
        newStack.setDurability(durability);
        newStack.addEnchantments(enchantmentList);
        ItemMeta newMeta = newStack.getItemMeta();
        newMeta.setLore(lore);
        newMeta.setDisplayName(displayName);
        newStack.setItemMeta(newMeta);
        return newStack;
    }
}