package com.togedy.togedy_server_v2.domain.schedule.entity;

import com.togedy.togedy_server_v2.domain.schedule.dto.PatchCategoryRequest;
import com.togedy.togedy_server_v2.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "color")
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Category(User user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
    }

    public void update(PatchCategoryRequest request) {
        if (!request.getCategoryName().isBlank()) {
            this.name = request.getCategoryName();
        }
        if (!request.getCategoryColor().isBlank()) {
            this.color = request.getCategoryColor();
        }
    }

}
