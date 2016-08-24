package com.opitzconsulting.entitymatcher;

import com.opitzconsulting.utils.ReflectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.opitzconsulting.utils.ReflectionUtils.getPropertyNamesFromInstanceAsList;
import static java.lang.String.format;

/**
 * Matches a given Entity's Properties.
 *
 * There are 3 different ways this matcher can be used:
 * <ol>
 *   <li>matchesAllProperties: validates all properties of the given instance</li>
 *   <li>matchesAllPropertiesExcluding validates all properties of the given instance excluding the named properties</li>
 *   <li>matchesSpecifiedProperties validates only the specified properties</li>
 * </ol>
 *
 * <p>
 *  Example Output of <em>matchesAllProperties</em>:
 *   <pre>{@code
 *
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
}</pre>
 * @param <T> the Type this matcher should verify.
 *
 *  @author Stefan Lack
 */
public class EntityMatcher<T> extends TypeSafeMatcher<T> {

  private final T expected;
  private final String[] fieldNames;
  private final List<InvalidProperty> invalidProperties;
  private EntityMatcher( T expected, MatcherType matcherType, String... fieldNames ) {
    this.expected = expected;
    if ( matcherType.equals( MatcherType.INCLUDE_NAMED_FIELDS ) ) {
      List<String> l = Arrays.asList( fieldNames );
      Collections.sort( l );
      this.fieldNames = l.toArray( new String[l.size()] );
    } else {
      // excludes named properties from list of all found properties...
      final List<String> allProperties = getPropertyNamesFromInstanceAsList( expected );
      allProperties.removeAll( Arrays.asList( fieldNames ) );
      this.fieldNames = allProperties.toArray( new String[allProperties.size()] );

    }
    this.invalidProperties = new ArrayList<>();

  }

  /**
   * Returns Matcher that checks, if every single property value of a given instance is equal to the properties of an other instance.
   *
   *  <em>Note</em>: this implementation is very similar to org.hamcrest.Matchers.samePropertyValuesAs(Person).
   *  Differences to the Hamcrest Matcher: while the hamcrest matcher only reports the first property value that does not match, this implementation do return a
   *  complete list.
   *
   * @param <T> type of class this matcher should be created for
   * @param expected an instance that defines the expected property values
   * @return the configured matcher
   */
  public static <T> EntityMatcher<T> matchesAllProperties( T expected ) {
    return new EntityMatcher<>( expected, MatcherType.EXCLUDE_NAMED_FIELDS );
  }

  /**
   *
   * Returns Matcher that checks, if the valud of every specified property of a given instance is
   * equal to the specified properties of an other instance.
   *
   * @param <T> type of class this matcher should be created for
   * @param expected an instance that defines the expected property values
   * @param propertyNames an array containing the names of the properties that should be validated
   * @return the configured matcher
   */
  public static <T> EntityMatcher<T> matchesSpecifiedProperties( T expected,
    String... propertyNames ) {
    return new EntityMatcher<>( expected, MatcherType.INCLUDE_NAMED_FIELDS, propertyNames );
  }

  /**
   *  Returns Matcher that checks, if every single property
   *  excluding the specified properties of a given instance is equal to the properties of an other instance.
   *
   * @param <T> type of class this matcher should be created for
   * @param expected an instance that defines the expected property values
   * @param propertyNames an array containing the names of the properties that should be excluded from validation
   * @return the configured matcher
   */
  public static <T> EntityMatcher<T> matchesAllPropertiesExcluding( T expected,
    String... propertyNames ) {
    return new EntityMatcher<>( expected, MatcherType.EXCLUDE_NAMED_FIELDS, propertyNames );
  }

  public void describeTo( Description description ) {
    description.appendText( "a entity with specified property values" );
  }

  @Override
  protected void describeMismatchSafely( T actual, Description mismatchDescription ) {
    if ( invalidProperties.size() > 0 ) {
      int maxLengthOfPropertyName = invalidProperties
        .stream()
        .mapToInt( InvalidProperty::getLengthOfPropertyName )
        .max()
        .getAsInt();

      mismatchDescription
        .appendText( format( "got entity with %d invalid values", invalidProperties.size() ) );

      mismatchDescription.appendText( Arrays.toString(
        invalidProperties.stream().map( p -> p.getMessage( maxLengthOfPropertyName ) )
          .toArray() ) );
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
        invalidProperties
          .add( new InvalidProperty( fieldName, expectedValue, actualValue ) );
      }
    }
    return invalidProperties.size() == 0;
  }

  private String descriptionOf( T object ) {
    ToStringBuilder toStringBuilder = new ToStringBuilder( object,
      ToStringStyle.SHORT_PREFIX_STYLE );
    for ( String fieldName : fieldNames ) {
      Object value = ReflectionUtils.getPropertyValueFromInstance( fieldName, object );
      toStringBuilder.append( fieldName, value );
    }
    return toStringBuilder.toString();
  }

  enum MatcherType {EXCLUDE_NAMED_FIELDS, INCLUDE_NAMED_FIELDS}
}

class InvalidProperty {
  private final String fieldName;
  private final Object expected;
  private final Object actual;

  public InvalidProperty( String propertyName, Object expected, Object actual ) {
    this.fieldName = propertyName;
    this.expected = expected;
    this.actual = actual;

  }

  public int getLengthOfPropertyName() {
    return fieldName.length();
  }

  public String getMessage( int maxLengthOfPropertyName ) {
    return format( "\n\t-->%s\t(expected:%s, actual:%s)",
      padRight( fieldName, maxLengthOfPropertyName ), expected, actual );
  }

  private String padRight( String s, int n ) {
    return format( "%1$-" + n + "s", s );
  }
}
