package com.opitzconsulting.entitymatcher;

import org.junit.Test;

import static com.opitzconsulting.entitymatcher.EntityMatcher.matchesAllProperties;
import static com.opitzconsulting.entitymatcher.EntityMatcher.matchesSpecifiedProperties;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class EntityMatcherTest {

  @Test
  public void testMatchesSpecifiedProperties() {
    Person actualPerson = new Person( "Stefan", "Lack" ).withEmail( "x1" );

    Person expectedPersion = new Person( "Hans", "Maier" ).withEmail( "x2" );

    assertThat( actualPerson,
      matchesSpecifiedProperties( expectedPersion, "firstName", "lastName" ) );
  }

  @Test
  public void testMatchesAllProperties() {

    Person actualPerson =
      new Person( "Hans", "Maier" )
        .withAge( 22 )
        .withEmail( "a.b" );

    assertThat( actualPerson,
      matchesAllProperties( new Person( "Stefan", "Lack" )
        .withAge( 20 )
        .withEmail( "a.b" ) ) );
  }

  @Test
  public void testSamePropertyValuesAsMatcher() {
    assertThat(
      new Person( "Peter", "Müller" ),
      samePropertyValuesAs( new Person( "Hans", "Maier" ) ) );
  }

}
