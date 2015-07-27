package tr.com.t2.labs.domain;

import info.archinnov.achilles.annotations.Column;
import info.archinnov.achilles.annotations.Entity;
import info.archinnov.achilles.annotations.PartitionKey;
import info.archinnov.achilles.annotations.TimeUUID;

import java.util.UUID;

/**
 * Created by mertcaliskan
 * on 24/07/15.
 */
@Entity(keyspace = "DEMO")
public class Person {

    @PartitionKey(1)
    @TimeUUID
    private UUID id;

    @Column
    private String name;
    @Column
    private String lastName;

    public Person() {
    }

    public Person(UUID id, String name, String lastName) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
