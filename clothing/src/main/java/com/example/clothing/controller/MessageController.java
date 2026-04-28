package com.example.clothing.controller;

import com.example.clothing.entity.Message;
import com.example.clothing.entity.User;
import com.example.clothing.repository.MessageRepository;
import com.example.clothing.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取用户的所有对话
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            List<Message> messages = messageRepository.findUserConversations(userId);
            List<Map<String, Object>> conversations = new ArrayList<>();

            for (Message message : messages) {
                Long otherUserId = message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId();
                User otherUser = userRepository.findById(otherUserId).orElse(null);
                if (otherUser != null) {
                    Map<String, Object> conversation = new HashMap<>();
                    conversation.put("id", otherUserId);
                    conversation.put("name", otherUser.getUsername());
                    conversation.put("avatar", "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%2C%20minimalist%2C%20friendly%20face&image_size=square");
                    conversation.put("lastMessage", message.getContent());
                    conversation.put("time", message.getCreatedAt());
                    conversation.put("unread", message.getReceiverId().equals(userId) && message.getStatus().equals("sent") ? 1 : 0);
                    conversations.add(conversation);
                }
            }

            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取对话列表失败\"}");
        }
    }

    // 获取与特定用户的消息历史
    @GetMapping("/history/{otherUserId}")
    @Transactional
    public ResponseEntity<?> getMessageHistory(@PathVariable Long otherUserId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, otherUserId);
            List<Map<String, Object>> messageHistory = new ArrayList<>();

            for (Message message : messages) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("id", message.getId());
                msg.put("content", message.getContent());
                msg.put("time", message.getCreatedAt());
                msg.put("sent", message.getSenderId().equals(userId));
                msg.put("status", message.getStatus());
                messageHistory.add(msg);
            }

            // 标记消息为已读
            messageRepository.markMessagesAsRead(userId, otherUserId);

            return ResponseEntity.ok(messageHistory);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"获取消息历史失败\"}");
        }
    }

    // 发送消息
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            Long receiverId = Long.parseLong(requestBody.get("receiverId").toString());
            String content = requestBody.get("content").toString();

            if (receiverId == null || content == null || content.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"接收者ID和消息内容不能为空\"}");
            }

            Message message = new Message();
            message.setSenderId(userId);
            message.setReceiverId(receiverId);
            message.setContent(content);
            message.setStatus("sent");

            Message savedMessage = messageRepository.save(message);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedMessage.getId());
            response.put("content", savedMessage.getContent());
            response.put("time", savedMessage.getCreatedAt());
            response.put("sent", true);
            response.put("status", savedMessage.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"发送消息失败\"}");
        }
    }

    // 标记消息为已读
    @PutMapping("/read/{otherUserId}")
    @Transactional
    public ResponseEntity<?> markAsRead(@PathVariable Long otherUserId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }

            messageRepository.markMessagesAsRead(userId, otherUserId);
            return ResponseEntity.ok("{\"message\": \"消息已标记为已读\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"标记消息为已读失败\"}");
        }
    }
}
