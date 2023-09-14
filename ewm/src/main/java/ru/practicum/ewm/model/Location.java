package ru.practicum.ewm.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "locations", schema = "public")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private float lat;

    @Column
    private float lon;
}
