package com.finalproject.courseevaluation_studentattendance.Model;


import javax.persistence.*;
import java.util.Set;

@Entity
public class Person {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String mNumber;

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    public String email;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(joinColumns = @JoinColumn(name="person_id"),
          inverseJoinColumns=@JoinColumn(name="role_id"))
  private Set<PersonRole>personRoles;

  @ManyToMany
  private Set<Course>courseinstructor;

  @ManyToMany
  private Set<Course>coursestudent;


    public Person()
    { }

//    @Override
//    public String toString() {
//        return "Person{" +
//                "id=" + id +
//                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", email='" + email + '\'' +
//                ", role=" + role +
//                ", course=" + course +
//                '}';
//    }

    public void addCourse(Course course)
    {


    }

//    public void removeCourse(Course course1)
//    {
//    }
//    public void deleteCourse(Course course)
//    {
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public Set<PersonRole> getPersonRoles() {
        return personRoles;
    }

    public void setPersonRoles(Set<PersonRole> personRoles) {
        this.personRoles = personRoles;
    }

    public Set<Course> getCourseinstructor() {
        return courseinstructor;
    }

    public void setCourseinstructor(Set<Course> courseinstructor) {
        this.courseinstructor = courseinstructor;
    }

    public Set<Course> getCoursestudent() {
        return coursestudent;
    }

    public void setCoursestudent(Set<Course> coursestudent) {
        this.coursestudent = coursestudent;
    }

    public void addRole(PersonRole p)
    {
        this.personRoles.add(p);
    }

    public void addInstructor(Course in){
        this.courseinstructor.add(in);
    }

    public void addStudents(Course st)
    {
        this.coursestudent.add(st);
    }
}
