package com.opitzconsulting.entitymatcher.examples;

import com.opitzconsulting.entitymatcher.Person;
import org.junit.Ignore;
import org.junit.Test;

import static com.opitzconsulting.entitymatcher.EntityMatcher.matchesAllProperties;
import static org.junit.Assert.assertThat;

public class ExampleEntityMatcher {

  @Test
  @Ignore("Ignored, since this demonstration code and is expected to fail!")
  public void testing_single_properties_with_matchesAllProperties() {
    final Person expected = new Person( "Maier", "Hans" ).withAge( 42 );
    final Person actual = new Person( "Mayer", "Hans" ).withAge( 7 );

    assertThat( actual, matchesAllProperties( expected ) );
  }
}