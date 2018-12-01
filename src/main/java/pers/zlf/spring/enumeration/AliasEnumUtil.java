package pers.zlf.spring.enumeration;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AliasEnumUtil {

    private static final ConcurrentMap<Class<?>, Map<String, ? extends AliasEnum>> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends AliasEnum> E valueOf(Class<E> classType, String alias) {
        Map<String, ? extends AliasEnum> aliases = CACHE.get(classType);
        if (aliases == null) {
            aliases = getEnumAliases(classType);
            CACHE.putIfAbsent(classType, aliases);
        }

        AliasEnum aliasEnum = aliases.get(alias);
        if (aliasEnum == null) {
            throw new IllegalArgumentException("Can not resolve alias [" + alias + "] for type [" + classType + "]");
        }

        return (E) aliasEnum;
    }

    @SuppressWarnings("unchecked")
    private static <E extends AliasEnum> Map<String, E> getEnumAliases(Class<E> classType) {
        if (!classType.isEnum()) {
            throw new MalformedAliasEnumException(classType + " not an enum");
        }

        Set<E> all = EnumSet.allOf(((Class) classType));
        HashMap<String, E> map = new HashMap<>();
        for (E aliasEnum : all) {
            Collection<String> aliases = aliasEnum.getAliases();
            if (aliases == null || aliases.isEmpty()) {
                throw new MalformedAliasEnumException("No alias found for " + classType);
            }

            for (String alias : aliases) {
                putDistinct(map, alias, aliasEnum);
            }

            putDistinct(map, ((Enum) aliasEnum).name(), aliasEnum);
        }

        return map;
    }

    private static <V extends AliasEnum> void putDistinct(Map<String, V> map, String key, V value) {
        Object prev = map.put(key, value);
        if (prev != null) {
            throw new MalformedAliasEnumException("Duplicate alias [" + key + "] among [" + value + ", " + prev + "]");
        }
    }
}
