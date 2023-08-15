package com.ssomar.score.hardness;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.ssomar.score.SCore;
import com.ssomar.score.hardness.hardness.manager.HardnessesManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HardnessesHandler {

    //public static final List<HardnessModifier> MODIFIERS = new ArrayList<>();
    private final Map<Location, BukkitScheduler> breakerPerLocation = new HashMap<>();
    private final ProtocolManager protocolManager;
    private final PacketAdapter listener = new PacketAdapter(SCore.plugin, ListenerPriority.LOW, PacketType.Play.Client.BLOCK_DIG) {
        @Override
        public void onPacketReceiving(final PacketEvent event) {
            final PacketContainer packet = event.getPacket();
            final Player player = event.getPlayer();
            final ItemStack item = player.getInventory().getItemInMainHand();
            if (player.getGameMode() == GameMode.CREATIVE) return;

            final StructureModifier<BlockPosition> dataTemp = packet.getBlockPositionModifier();
            final StructureModifier<EnumWrappers.Direction> dataDirection = packet.getDirections();
            final StructureModifier<EnumWrappers.PlayerDigType> data = packet
                    .getEnumModifier(EnumWrappers.PlayerDigType.class, 2);
            EnumWrappers.PlayerDigType type;

            try {
                type = data.getValues().get(0);
            } catch (IllegalArgumentException exception) {
                type = EnumWrappers.PlayerDigType.SWAP_HELD_ITEMS;
            }

            final BlockPosition pos = dataTemp.getValues().get(0);
            final World world = player.getWorld();
            final Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
            final BlockFace blockFace = dataDirection.size() > 0 ? BlockFace.valueOf(dataDirection.read(0).name()) : BlockFace.UP;

            HardnessModifier triggeredModifier = null;
            for (final HardnessModifier modifier : HardnessesManager.getInstance().getAllObjects())
                if (modifier.isTriggered(player, block, item)) {
                    triggeredModifier = modifier;
                    break;
                }
            if (triggeredModifier == null) return;
            final long period = triggeredModifier.getPeriod(player, block, item);
            if (period == 0) return;

            event.setCancelled(true);

            final Location location = block.getLocation();
            if (type == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {

                //SsomarDev.testMsg("START_DESTROY_BLOCK", true);

                Bukkit.getScheduler().runTask(SCore.plugin, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (period * 11), Integer.MAX_VALUE, false, false, false)));
                if (breakerPerLocation.containsKey(location))
                    breakerPerLocation.get(location).cancelTasks(SCore.plugin);

                final BukkitScheduler scheduler = Bukkit.getScheduler();
                // Cancellation state is being ignored.
                // However still needs to be called for plugin support.
                final PlayerInteractEvent playerInteractEvent = new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), block, blockFace, EquipmentSlot.HAND);
                scheduler.runTask(SCore.plugin, () -> Bukkit.getPluginManager().callEvent(playerInteractEvent));

                // If the relevant damage event is cancelled, return
                if (blockDamageEventCancelled(block, player)) return;

                breakerPerLocation.put(location, scheduler);
                final HardnessModifier modifier = triggeredModifier;

                //SsomarDev.testMsg("before runTaskTimer", true);
                scheduler.runTaskTimer(SCore.plugin, new Consumer<BukkitTask>() {
                    int value = 0;

                    @Override
                    public void accept(final BukkitTask bukkitTask) {
                        // SsomarDev.testMsg("accept runTaskTimer", true);

                        if (!breakerPerLocation.containsKey(location)) {
                            bukkitTask.cancel();
                            return;
                        }

                        if (item.getEnchantmentLevel(Enchantment.DIG_SPEED) >= 5)
                            value = 10;

                        for (final Entity entity : world.getNearbyEntities(location, 16, 16, 16))
                            if (entity instanceof Player) {
                                Player viewer = (Player) entity;
                                sendBlockBreak(viewer, location, value);
                            }

                        if (value++ < 10) return;

                        final BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
                        Bukkit.getPluginManager().callEvent(blockBreakEvent);

                        if (!blockBreakEvent.isCancelled() /* && ProtectionLib.canBreak(player, block.getLocation()) */) {
                            modifier.breakBlock(player, block, item);
                            PlayerItemDamageEvent playerItemDamageEvent = new PlayerItemDamageEvent(player, item, 1);
                            Bukkit.getPluginManager().callEvent(playerItemDamageEvent);
                        }

                        Bukkit.getScheduler().runTask(SCore.plugin, () ->
                                player.removePotionEffect(PotionEffectType.SLOW_DIGGING));
                        breakerPerLocation.remove(location);
                        for (final Entity entity : world.getNearbyEntities(location, 16, 16, 16))
                            if (entity instanceof Player) {
                                Player viewer = (Player) entity;
                                sendBlockBreak(viewer, location, 10);
                            }
                        bukkitTask.cancel();
                    }
                }, period, period);
            } else {
                Bukkit.getScheduler().runTask(SCore.plugin, () -> {
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    /* if (!ProtectionLib.canBreak(player, block.getLocation()))
                        player.sendBlockChange(block.getLocation(), block.getBlockData()); */

                    for (final Entity entity : world.getNearbyEntities(location, 16, 16, 16))
                        if (entity instanceof Player) {
                            Player viewer = (Player) entity;
                            sendBlockBreak(viewer, location, 10);
                        }
                    breakerPerLocation.remove(location);
                });
            }
        }
    };

    public HardnessesHandler() {
        protocolManager = SCore.protocolManager;
        //MODIFIERS.add(new RealHardnessModifier());
    }

    private boolean blockDamageEventCancelled(Block block, Player player) {
        return false;
    }

    private void sendBlockBreak(final Player player, final Location location, final int stage) {
        Block block = location.getBlock();
        final PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
        packet.getIntegers().write(0, location.hashCode()).write(1, stage);
        packet.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));

        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerListener() {
        protocolManager.addPacketListener(listener);
    }

    private String getSound(Block block) {
        return block.getBlockData().getSoundGroup().getHitSound().getKey().toString();
    }
}