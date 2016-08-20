package com.opitzconsulting.entitymatcher;

import org.junit.Test;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class ExamplesHamcrestSamePropertyValuesAs {

  @Test
  public void testing_single_properties_with_hamcrest_samePropertyValuesAs() {
    final Person expected = new Person( "Maier", "Hans" ).withAge( 42 );
    final Person actual = new Person( "Mayer", "Hans" ).withAge( 7 );

    assertThat( actual, samePropertyValuesAs( expected ) );
  }
}
