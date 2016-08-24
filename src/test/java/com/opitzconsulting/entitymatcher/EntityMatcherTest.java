package com.opitzconsulting.entitymatcher;

import org.junit.Test;

import static com.opitzconsulting.entitymatcher.EntityMatcher.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * @author Stefan Lack
 */
public class EntityMatcherTest {

  @Test
  public void testMatchesAllProperties() {

    Person actualPerson =
      new Person( "Duck", "Donald" )
        .withAge( 42 )
        .withEmail( "donald.duck@entenhausen.de" );

    assertThat( actualPerson,
      matchesAllProperties( new Person( "Duck", "Donald" )
        .withAge( 42 )
        .withEmail( "donald.duck@entenhausen.de" ) ) );
  }

  @Test
  public void testMatchesSpecifiedProperties() {
    Person actualPerson = new Person( "Duck", "Donald" ).withAge( 42 ).withEmail(
      "donald.duck@entenhausen.de" );
    Person expectedPerson = new Person( "Duck", "Daisy" ).withAge( 42 ).withEmail(
      "Daisy.duck@entenhausen.de" );

    assertThat( actualPerson,
      matchesSpecifiedProperties( expectedPerson, "lastName", "age" ) );

    assertThat( actualPerson,
      not( matchesSpecifiedProperties( expectedPerson, "firstName" ) ) );
  }

  @Test
  public void testMatchesAllPropertiesExcluding() {
    Person actualPerson = new Person( "Duck", "Donald" ).withAge( 42 ).withEmail(
      "donald.duck@entenhausen.de" );
    Person expectedPerson = new Person( "Duck", "Daisy" ).withAge( 42 )
      .withEmail( "Daisy.duck@entenhausen.de" );

    assertThat( actualPerson,
      matchesAllPropertiesExcluding( expectedPerson, "firstName", "email" ) );
  }

  @Test
  public void testReportsNotExistingProperties() {
    Person actualPerson = new Person( "Duck", "Donald" ).withEmail( "donald.duck@entenhausen.de" );
    Person expectedPerson = new Person( "Duck", "Daisy" ).withEmail( "Daisy.duck@entenhausen.de" );

    try {
      assertThat( actualPerson,
        matchesSpecifiedProperties( expectedPerson, "notExistingProperty" ) );
    } catch ( RuntimeException expectedException ) {
      assertThat( "Exception cause", expectedException.getMessage(),
        containsString( "Field notExistingProperty not found in class" ) );
    }
  }
}
