package be.rubus.microstream.data.model;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.UUID;

@Entity
public class Version {

    @Id
    private String id = UUID.randomUUID().toString();
    @Column
    private final int value;

    public Version(int value) {
        this.value = value;
    }

    public Version(Version version, int value) {
        this.id = version.id;  // keep same id.
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }
}
