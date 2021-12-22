package cn.t.freedns.util;

import java.util.Collection;
import java.util.Map;

/**
 * CollectionUtil
 *
 * @author <a href="mailto:yangjian@liby.ltd">研发部-杨建</a>
 * @version V1.0
 * @since 2021-12-22 17:40
 **/
public class CollectionUtil {
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}