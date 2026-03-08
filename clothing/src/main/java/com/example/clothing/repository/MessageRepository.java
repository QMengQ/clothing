package com.example.clothing.repository;

import com.example.clothing.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 获取两个用户之间的消息
    @Query("SELECT m FROM Message m WHERE (m.senderId = :userId1 AND m.receiverId = :userId2) OR (m.senderId = :userId2 AND m.receiverId = :userId1) ORDER BY m.createdAt ASC")
    List<Message> findMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // 获取用户的所有对话（与其他用户的最新消息）
    @Query("SELECT m FROM Message m WHERE m.id IN (SELECT MAX(m2.id) FROM Message m2 WHERE m2.senderId = :userId OR m2.receiverId = :userId GROUP BY CASE WHEN m2.senderId = :userId THEN m2.receiverId ELSE m2.senderId END) ORDER BY m.createdAt DESC")
    List<Message> findUserConversations(@Param("userId") Long userId);

    // 获取用户的未读消息数量
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.status = 'sent'")
    long countUnreadMessages(@Param("userId") Long userId);

    // 标记消息为已读
    @Modifying
    @Query("UPDATE Message m SET m.status = 'read' WHERE m.receiverId = :userId AND m.senderId = :otherUserId AND m.status = 'sent'")
    void markMessagesAsRead(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
}
