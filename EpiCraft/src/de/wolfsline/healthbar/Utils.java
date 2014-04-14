package de.wolfsline.healthbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Utils {

	private Utils() {
		
	}
	
	public static String replaceSymbols(String input) {

		if (input == null || input.length() == 0) 
			return input;

		//replaces colors and symbols
		return ChatColor.translateAlternateColorCodes('&', input)
					.replace("<3", "\u2764")
					.replace("[x]", "\u2588")
					.replace("[/]", "\u2588")
					.replace("[*]", "\u2605")
					.replace("[p]", "\u25CF")		
					.replace("[+]", "\u25C6")
					.replace("[++]", "\u2726");
	}
	
	public static int round(double d) {
		double remainder = d - (int) d;
		if (remainder <= 0.5) {
			return (int) d;
		} else {
			return ((int) d) + 1;
		}
	}
	
	public static int roundUpPositive(double d) {
	    int i = (int) d;
	    double remainder = d - i;
	    if (remainder > 0.0) {
	    	i++;
	    }
	    if (i<0) return 0;
	    return i;
	}

	public static int roundUpPositiveWithMax(double d, int max) {
	   int result = roundUpPositive(d);
	   if (d > max) return max;
	   return result;
	}
	
	public static String getBukkitBuild() {
		String version = Bukkit.getVersion();
		Pattern pattern = Pattern.compile("(b)([0-9]+)(jnks)");
		Matcher matcher = pattern.matcher(version);

		if (matcher.find()) {
		return matcher.group(2);
		}

		return null;
	}
	
}
