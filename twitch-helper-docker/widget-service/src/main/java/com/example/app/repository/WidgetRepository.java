package com.example.repository;

import com.example.entity.Widget;
import com.example.entity.WidgetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WidgetRepository extends JpaRepository<Widget, Long> {
    Optional<Widget> findByPublicToken(String publicToken);

    List<Widget> findByUserId(Long userId);

    List<Widget> findByUserIdAndTypeAndEnabledTrue(Long userId, WidgetType type);
}