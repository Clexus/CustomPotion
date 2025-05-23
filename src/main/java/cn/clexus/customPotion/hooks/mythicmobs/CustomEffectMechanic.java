package cn.clexus.customPotion.hooks.mythicmobs;

import cn.clexus.customPotion.effects.CustomEffect;
import cn.clexus.customPotion.effects.CustomEffectType;
import cn.clexus.customPotion.effects.EffectRegistry;
import cn.clexus.customPotion.effects.StackingModes;
import cn.clexus.customPotion.managers.PotionManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import java.io.File;


public class CustomEffectMechanic extends SkillMechanic implements ITargetedEntitySkill {
    private String effectName;
    private int duration;
    private int level;
    private StackingModes stackingMode;

    public CustomEffectMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
        super(manager, file, line, mlc);
        this.line = line;
        this.threadSafetyLevel = ThreadSafetyLevel.EITHER;

        this.effectName = mlc.getString(new String[]{"effect", "e", "type", "t"}, "poison");
        this.duration = mlc.getInteger(new String[]{"duration", "d"}, 1);
        this.level = mlc.getInteger(new String[]{"level", "l"}, 1);
        this.stackingMode = mlc.getEnum(new String[]{"stackingmode", "sm", "s"}, StackingModes.class, StackingModes.NORMAL);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        CustomEffectType type = EffectRegistry.getById(effectName);
        if (type == null) {
            return SkillResult.ERROR;
        }
        LivingEntity entity = (LivingEntity) target.getBukkitEntity();
        PotionManager.addEffect(entity, new CustomEffect(type, duration, level, (LivingEntity) data.getCaster().getEntity().getBukkitEntity()), stackingMode);
        return SkillResult.SUCCESS;
    }
}
