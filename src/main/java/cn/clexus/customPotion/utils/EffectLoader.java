package cn.clexus.customPotion.utils;

import cn.clexus.customPotion.effects.CustomEffectType;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.Set;

public class EffectLoader {

    public static void registerAllEffects() {
        Reflections reflections = new Reflections("cn.clexus.customPotion.effects.types");

        Set<Class<? extends CustomEffectType>> effectClasses = reflections.getSubTypesOf(CustomEffectType.class);

        for (Class<? extends CustomEffectType> effectClass : effectClasses) {
            if (Modifier.isAbstract(effectClass.getModifiers())) {
                continue;
            }

            try {
                effectClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
