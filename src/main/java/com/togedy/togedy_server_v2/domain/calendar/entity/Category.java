package com.togedy.togedy_server_v2.domain.calendar.entity;

import com.togedy.togedy_server_v2.global.entity.BaseEntity;
import com.togedy.togedy_server_v2.global.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "status")
    private String status = Status.ACTIVE.getStatus();

    @Builder
    public Category(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
