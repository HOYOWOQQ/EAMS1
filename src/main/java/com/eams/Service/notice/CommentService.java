package com.eams.Service.notice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.member.Member;
import com.eams.Entity.notice.Comment;
import com.eams.Entity.notice.DTO.CommentCreateDTO;
import com.eams.Entity.notice.DTO.CommentDTO;
import com.eams.Entity.notice.DTO.CommentUpdateDTO;
import com.eams.Repository.member.MemberRepository;
import com.eams.Repository.notice.CommentRepository;

@Service
@Transactional
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    // 根據通知ID獲取留言列表
    public List<CommentDTO> getCommentsByNoticeId(Integer noticeId) {
        List<Comment> mainComments = commentRepository.findMainCommentsByNoticeId(noticeId);
        List<CommentDTO> commentDTOs = new ArrayList<>();
        
        for (Comment comment : mainComments) {
            CommentDTO dto = convertToDTO(comment);
            // 獲取回覆
            List<Comment> replies = commentRepository.findRepliesByParentCommentId(comment.getCommentId());
            List<CommentDTO> replyDTOs = new ArrayList<>();
            for (Comment reply : replies) {
                replyDTOs.add(convertToDTO(reply));
            }
            dto.setReplies(replyDTOs);
            commentDTOs.add(dto);
        }
        
        return commentDTOs;
    }
    
    // 創建留言
    public CommentDTO createComment(CommentCreateDTO createDTO, Integer userId) {
        Comment comment = new Comment(createDTO.getNoticeId(), userId, 
                                    createDTO.getContent(), createDTO.getParentCommentId());
        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }
    
    // 更新留言
    public CommentDTO updateComment(Integer commentId, CommentUpdateDTO updateDTO, Integer userId) {
        Optional<Comment> commentOpt = commentRepository.findActiveCommentById(commentId);
        if (!commentOpt.isPresent()) {
            return null;
        }
        
        Comment comment = commentOpt.get();
        
        // 檢查權限（只有留言作者可以編輯）
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("您沒有權限編輯此留言");
        }
        
        int result = commentRepository.updateCommentContent(commentId, updateDTO.getContent());
        if (result > 0) {
            comment.setContent(updateDTO.getContent());
            return convertToDTO(comment);
        }
        
        return null;
    }
    
    // 刪除留言
    public boolean deleteComment(Integer commentId, Integer userId, String userRole) {
        Optional<Comment> commentOpt = commentRepository.findActiveCommentById(commentId);
        if (!commentOpt.isPresent()) {
            return false;
        }
        
        Comment comment = commentOpt.get();
        
        // 檢查權限（留言作者或教師可以刪除）
        if (!canDeleteComment(commentId, userId, userRole)) {
            throw new RuntimeException("您沒有權限刪除此留言");
        }
        
        int result = commentRepository.softDeleteComment(commentId);
        return result > 0;
    }
    
    // 檢查是否可以刪除留言
    public boolean canDeleteComment(Integer commentId, Integer userId, String userRole) {
        // 教師可以刪除任何留言
        if ("teacher".equals(userRole)) {
            return true;
        }
        
        // 其他用戶只能刪除自己的留言
        return commentRepository.isCommentOwner(commentId, userId);
    }
    
    // 檢查是否可以編輯留言
    public boolean canEditComment(Integer commentId, Integer userId) {
        return commentRepository.isCommentOwner(commentId, userId);
    }
    
    // 轉換為DTO
    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setNoticeId(comment.getNoticeId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setIsActive(comment.getIsActive());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        // 設置用戶信息
        Optional<Member> userOpt = memberRepository.findById(comment.getUserId());
        if (userOpt.isPresent()) {
            Member user = userOpt.get();
            dto.setUserName(user.getName());
            dto.setUserRole(user.getRole());
        }
        
        return dto;
    }
}
