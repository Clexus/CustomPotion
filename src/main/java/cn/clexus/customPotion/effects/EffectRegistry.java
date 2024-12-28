package cn.clexus.customPotion.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collections;
import java.util.Map;

public class EffectRegistry {

    private static final BiMap<String, CustomEffectType> ID_TO_TYPE = HashBiMap.create();

    /**
     * 注册效果类型
     *
     * @param type CustomEffectType 对象
     */
    public static void register(CustomEffectType type) {
        if (ID_TO_TYPE.containsKey(type.getId()) || ID_TO_TYPE.containsValue(type)) {
            throw new IllegalArgumentException("CustomEffectType with the same ID or DisplayName already exists: " + type);
        }

        ID_TO_TYPE.put(type.getId(), type);
    }

    /**
     * 根据 ID 获取效果类型
     *
     * @param id 效果类型 ID
     * @return 对应的 CustomEffectType 或 null
     */
    public static CustomEffectType getById(String id) {
        return ID_TO_TYPE.get(id);
    }

    /**
     * 根据显示名获取效果类型
     *
     * @param displayName 效果类型显示名
     * @return 对应的 CustomEffectType 或 null
     */
    public static CustomEffectType getByDisplayName(String displayName) {
        return ID_TO_TYPE.values().stream()
                .filter(customEffectType -> customEffectType.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有注册的效果类型
     *
     * @return 不可修改的 Map，其中 key 是 ID，value 是 CustomEffectType
     */
    public static Map<String, CustomEffectType> getAllEffects() {
        return Collections.unmodifiableMap(ID_TO_TYPE);
    }

    /**
     * 清除所有注册的效果类型（仅供调试或热重载使用）
     */
    public static void clear() {
        ID_TO_TYPE.clear();
    }
}
