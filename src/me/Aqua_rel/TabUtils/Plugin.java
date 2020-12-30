package me.Aqua_rel.TabUtils;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.minecraft.server.v1_16_R1.ChatComponentText;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerListHeaderFooter;

public class Plugin extends JavaPlugin implements Listener {
	public long tickTime = 0;
	public long oldTime;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				tickTime = (System.currentTimeMillis() - oldTime) / 10;
				oldTime = System.currentTimeMillis();

				for (Player p : Bukkit.getOnlinePlayers()) {
					setTablist(p, tickTime);
				}
			}
		}, 0L, 10L);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		setTablist(p, tickTime);
	}

	public void setTablist(Player p, long mspt) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		Object head = new ChatComponentText(getConfig().getString("header"));
		// tickTime = (System.currentTimeMillis() - oldTime) / 10;
		String color;
		if (mspt < 60) {
			color = "\u00a7aMspt: \u00a7l";
		} else if (mspt < 100) {
			color = "\u00a7eMspt: \u00a7l";
		} else if (mspt < 150) {
			color = "\u00a7cMspt: \u00a7l";
		} else if (mspt < 200) {
			color = "\u00a7dMspt: \u00a7l";
		} else {
			color = "\u00a71Tick Time: \u00a7l";
		}
		Object foot = new ChatComponentText(color + mspt + "ms");
		// oldTime = System.currentTimeMillis();

		try {
			Field a = packet.getClass().getDeclaredField("header");
			a.setAccessible(true);
			Field b = packet.getClass().getDeclaredField("footer");
			b.setAccessible(true);

			a.set(packet, head);
			b.set(packet, foot);

			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}
}
