package com.opitzconsulting.entitymatcher;

public class Person {

  String firstName;
  String lastName;
  private String email;
  private int age;

  public Person( String lastName, String firstName ) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Person withEmail( String email ) {
    this.email = email;
    return this;
  }

  public Person withAge( int age ) {
    this.age = age;
    return this;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public int getAge() {
    return age;
  }
}
