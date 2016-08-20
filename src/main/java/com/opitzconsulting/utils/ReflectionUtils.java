package com.opitzconsulting.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReflectionUtils {

  private ReflectionUtils() {

  }

  public static Object getPropertyValueFromInstance( String propertyName, Object instance ) {
    Object value = null;
    Class clazz = instance.getClass();
    try {

      Field field = getDeclaredOrInheritedField( propertyName, clazz );
      field.setAccessible( true );
      value = field.get( instance );
    } catch ( Exception e ) {
      throw new RuntimeException( String.format( "Cannot read field '%s' from Entity '%s': %s",
        propertyName,
        clazz.getName(),
        e.getMessage() ), e );
    }
    return value;
  }

  private static Field getDeclaredOrInheritedField( String fieldName, Class<?> clazz )
    throws NoSuchFieldException {
    try {
      Field declaredField = clazz.getDeclaredField( fieldName );
      return declaredField;
    } catch ( NoSuchFieldException e ) {
      Class<?> superclass = clazz.getSuperclass();
      if ( superclass != null ) {
        return getDeclaredOrInheritedField( fieldName, superclass );
      }
    }
    throw new NoSuchFieldException( String.format( "Field %s not found in class", fieldName ) );

  }

  public static String[] getPropertyNamesFromInstance( Object instance ) {
    List<String> propertyNames = getPropertyNamesFromInstanceAsList( instance );
    return propertyNames.toArray( new String[] { } );
  }

  public static List<String> getPropertyNamesFromInstanceAsList( Object instance ) {
    List<String> propertyNames = new ArrayList<String>();
    for ( Field field : getAllFields( instance.getClass() ) ) {
      propertyNames.add( field.getName() );
    }
    Collections.sort( propertyNames );
    return propertyNames;
  }

  private static List<Field> getAllFields( Class<?> type ) {
    List<Field> fields = new ArrayList<Field>();
    for ( Class<?> c = type; c != null; c = c.getSuperclass() ) {
      fields.addAll( Arrays.asList( c.getDeclaredFields() ) );
    }
    return fields;
  }
}
