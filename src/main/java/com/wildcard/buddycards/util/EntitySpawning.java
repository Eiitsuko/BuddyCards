package com.wildcard.buddycards.util;

import com.wildcard.buddycards.entities.EnderlingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntitySpawning {

    @SubscribeEvent
    public void spawnIn(LivingSpawnEvent event) {
        if (event.getEntity().tickCount == 0 && event.getEntity() instanceof EndermanEntity) {
            double rand = Math.random();
            if ((event.getEntity().level.dimension().equals(World.END) && rand < ConfigManager.enderlingChanceEnd.get()) ||
                    (event.getEntity().level.dimension().equals(World.NETHER) && rand < ConfigManager.enderlingChanceNether.get()) ||
                    (event.getEntity().level.dimension().equals(World.OVERWORLD) && rand < ConfigManager.enderlingChanceOver.get())) {
                EnderlingEntity entity = new EnderlingEntity(RegistryHandler.ENDERLING.get(), event.getEntity().level);
                entity.setPos(event.getX() + 1, event.getY(), event.getZ());
                event.getEntity().level.addFreshEntity(entity);
            }
        }
    }
}
