package com.opitzconsulting.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Stefan Lack
 */
public class ReflectionUtils {

  private ReflectionUtils() {

  }

  public static Object getPropertyValueFromInstance( String propertyName, Object instance ) {
    Class clazz = instance.getClass();
    try {

      Field field = getDeclaredOrInheritedField( propertyName, clazz );
      field.setAccessible( true );
      return field.get( instance );
    } catch ( Exception e ) {
      throw new RuntimeException( String.format( "Cannot read field '%s' from Entity '%s': %s",
        propertyName,
        clazz.getName(),
        e.getMessage() ), e );
    }
  }

  private static Field getDeclaredOrInheritedField( String fieldName, Class<?> clazz )
    throws NoSuchFieldException {
    try {
      return clazz.getDeclaredField( fieldName );
    } catch ( NoSuchFieldException e ) {
      Class<?> superclass = clazz.getSuperclass();
      if ( superclass != null ) {
        return getDeclaredOrInheritedField( fieldName, superclass );
      }
    }
    throw new NoSuchFieldException( String.format( "Field %s not found in class", fieldName ) );

  }

  public static List<String> getPropertyNamesFromInstanceAsList( Object instance ) {
    List<String> propertyNames = new ArrayList<>();
    for ( Field field : getAllFields( instance.getClass() ) ) {
      propertyNames.add( field.getName() );
    }
    Collections.sort( propertyNames );
    return propertyNames;
  }

  private static List<Field> getAllFields( Class<?> type ) {
    List<Field> fields = new ArrayList<>();
    for ( Class<?> c = type; c != null; c = c.getSuperclass() ) {
      fields.addAll( Arrays.asList( c.getDeclaredFields() ) );
    }
    return fields;
  }
}
