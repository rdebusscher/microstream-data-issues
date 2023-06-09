package be.rubus.microstream.data.model;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.time.LocalDate;

@Entity
public class Person {

    @Id
    private String id;

    @Column
    private String name;

    @Column
    private LocalDate birthday;


    private Person(String id, String name, LocalDate birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public LocalDate birthday() {
        return birthday;
    }

    public static Person of(String id, String name, LocalDate birthday) {
        return new Person(id, name, birthday);
    }
}
