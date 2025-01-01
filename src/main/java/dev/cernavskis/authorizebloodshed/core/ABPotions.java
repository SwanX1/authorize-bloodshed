package dev.cernavskis.authorizebloodshed.core;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class ABPotions {
    public static final Potion RESISTANCE = new Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600));
}
