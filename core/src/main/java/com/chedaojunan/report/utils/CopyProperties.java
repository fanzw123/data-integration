package com.chedaojunan.report.utils;

import java.lang.reflect.Field;

public class CopyProperties {

  /**
   * 复制对象属性（对象类型必须相同）
   *
   * @param orig       资源对象
   * @param dest       目标对象
   * @param clazz      源对象类
   * @param ignoreNull 是否忽略空（true:忽略，false:不忽略）
   * @return
   */
  public <T> T copyProperties(T orig, T dest, Class<?> clazz, boolean ignoreNull) {
    if (orig == null || dest == null)
      return null;
    if (!clazz.isAssignableFrom(orig.getClass()))
      return null;
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        Object value = field.get(orig);
        if (!java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
          if (!(ignoreNull && value == null)) {
            field.set(dest, value);
          }
        }
        field.setAccessible(false);
      } catch (Exception e) {
      }
    }

    if (clazz.getSuperclass() == Object.class) {
      return dest;
    }

    return copyProperties(orig, dest, clazz.getSuperclass(), ignoreNull);
  }

  /**
   * 复制对象属性（对象类型必须相同）
   *
   * @param orig       资源对象
   * @param dest       目标对象
   * @param ignoreNull 是否忽略空（true:忽略，false：不忽略）
   */
  public <T> T copyProperties(T orig, T dest, boolean ignoreNull) {
    if (orig == null || dest == null)
      return null;
    return copyProperties(orig, dest, orig.getClass(), ignoreNull);
  }

  /**
   * 复制对象
   *
   * @param src 资源对象
   * @return 新对象
   */
  public <T> T clone(T src) {
    if (src == null) {
      return null;
    }
    T newObject = null;
    try {
      newObject = (T) src.getClass().newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return copyProperties(src, newObject, false);
  }
}
