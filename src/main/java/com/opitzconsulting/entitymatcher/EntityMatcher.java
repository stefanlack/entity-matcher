package com.opitzconsulting.entitymatcher;

import static com.opitzconsulting.utils.ReflectionUtils.getPropertyNamesFromInstanceAsList;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.opitzconsulting.utils.ReflectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class EntityMatcher<T> extends TypeSafeMatcher<T> {

  enum MatcherType {EXCLUDE_NAMED_FIELDS, INCLUDE_NAMED_FIELDS}

  ;

  /**
   * Returns Matcher that checks, if every single property value of a given instance is equal to the properties of an other instance
   *
   *  <em>Note</em>: this implementation is very similar to org.hamcrest.Matchers.samePropertyValuesAs(Person).
   *  Differences to the Hamcrest Matcher: while the hamcrest matcher only reports the first property value that does not match, this implementation do return a
   *  complete list.
   *  Example Output:
   *   <pre>
   java.lang.AssertionError:
   Expected: a entity with specified property values
   but: got entity with 2 invalid values [
   -->age		(expected:42, actual:7),
   -->lastName		(expected:Maier, actual:Mayer)]
   ************
   Details:
   Actual properties: Person[age=7,email=<null>,firstName=Hans,lastName=Mayer]
   Expected properties:Person[age=42,email=<null>,firstName=Hans,lastName=Maier]
   Expected properties:Person[age=42,email=<null>,firstName=Hans,lastName=Maier]
   *   </pre>
   * @param expected
   * @return
   */
  public static <T> EntityMatcher<T> matchesAllProperties( T expected ) {
    return new EntityMatcher<T>( expected, MatcherType.EXCLUDE_NAMED_FIELDS );
  }

  public static <T> EntityMatcher<T> matchesSpecifiedProperties( T expected,
    String... fieldNames ) {
    return new EntityMatcher<T>( expected, MatcherType.INCLUDE_NAMED_FIELDS, fieldNames );
  }

  public static <T> EntityMatcher<T> matchesAllPropertiesExcluding( T expected,
    String... fieldNames ) {
    return new EntityMatcher<T>( expected, MatcherType.EXCLUDE_NAMED_FIELDS, fieldNames );
  }

  private final T expected;
  private final String[] fieldNames;
  private final List<InvalidFieldMessage> invalidFieldMessages;

  private EntityMatcher( T expected, MatcherType matcherType, String... fieldNames ) {
    this.expected = expected;
    if ( matcherType.equals( MatcherType.INCLUDE_NAMED_FIELDS ) ) {
      List<String> l = Arrays.asList( fieldNames );
      Collections.sort( l );
      this.fieldNames = l.toArray( new String[] { } );
    } else {
      // excludes named properties from list of all found properties...
      final List<String> allProperties = getPropertyNamesFromInstanceAsList( expected );
      allProperties.removeAll( Arrays.asList( fieldNames ) );
      this.fieldNames = allProperties.toArray( new String[] { } );

    }
    this.invalidFieldMessages = new ArrayList<InvalidFieldMessage>();
    MatcherType matcherType1 = matcherType;
  }

  public void describeTo( Description description ) {
    description.appendText( "a entity with specified property values" );
  }

  @Override
  protected void describeMismatchSafely( T actual, Description mismatchDescription ) {
    if ( invalidFieldMessages.size() > 0 ) {
      final String format = format( "got entity with %d invalid values ",
        invalidFieldMessages.size() );
      mismatchDescription.appendText( format )
        .appendText( Arrays.toString( invalidFieldMessages.toArray() ) );
    }
    mismatchDescription.appendText( "\n************\n Details:\n" )
      .appendText( "\t\tActual properties: " )
      .appendText( descriptionOf( actual ) )
      .appendText( "\n\t\tExpected properties:" )
      .appendText( descriptionOf( expected ) );
  }

  @Override
  protected boolean matchesSafely( T actual ) {
    for ( String fieldName : fieldNames ) {
      Object actualValue = ReflectionUtils.getPropertyValueFromInstance( fieldName, actual );
      Object expectedValue = ReflectionUtils.getPropertyValueFromInstance( fieldName, expected );
      if ( !ObjectUtils.equals( actualValue, expectedValue ) ) {
        invalidFieldMessages
          .add( new InvalidFieldMessage( fieldName, expectedValue, actualValue ) );
      }
    }
    return invalidFieldMessages.size() == 0;
  }

  private String descriptionOf( T object ) {
    ToStringBuilder toStringBuilder = new ToStringBuilder( object,
      ToStringStyle.SHORT_PREFIX_STYLE );
    for ( String fieldName : fieldNames ) {
      Object value = ReflectionUtils.getPropertyValueFromInstance( fieldName, object );
      toStringBuilder.append( fieldName, value );
    }
    final String result = toStringBuilder.toString();
    return result;
  }

  class InvalidFieldMessage {
    final String message;

    public InvalidFieldMessage( String fieldName, Object expected, Object actual ) {
      message = String
        .format( "\n\t-->%s\t\t(expected:%s, actual:%s)", fieldName, expected, actual );
    }

    public String getMessage() {
      return message;
    }

    @Override
    public String toString() {
      return message;
    }
  }

}
