package com.example.app.repository;

import com.example.app.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT m FROM ChatMessage m WHERE m.channel = :channel AND m.timestamp > :since ORDER BY m.timestamp DESC")
    List<ChatMessage> findTopByChannelAndTimestampAfterOrderByTimestampDesc(
            @Param("channel") String channel,
            @Param("since") Instant since
    );
    
    List<ChatMessage> findByChannelOrderByTimestampDesc(String channel);
    
    void deleteByTimestampBefore(Instant timestamp);
}