package com.opitzconsulting.entitymatcher;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExampleSimpleAssertStatements {

  @Test
  public void testing_single_properties_with_simple_assert_statements() {
    final Person expected = new Person( "Maier", "Hans" ).withAge( 42 );
    final Person actual = new Person( "Mayer", "Hans" ).withAge( 7 );

    assertEquals( "lastname correct", expected.getLastName(), actual.getLastName() );
    assertEquals( "firstname correct", expected.getFirstName(), actual.getFirstName() );
    assertEquals( "age correct", expected.getAge(), actual.getAge() );
  }

}
