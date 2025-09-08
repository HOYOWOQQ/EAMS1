package com.eams.Service.chat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.chat.MemberOnlineStatus;
import com.eams.Entity.chat.chatDTO.MemberOnlineStatusDTO;
import com.eams.Entity.member.Member;
import com.eams.Repository.chat.MemberOnlineStatusRepository;
import com.eams.Repository.member.MemberRepository;
@Service
@Transactional
public class OnlineStatusService {
    
    @Autowired
    private MemberOnlineStatusRepository onlineStatusRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    // 更新用戶在線狀態
    public void updateOnlineStatus(Integer memberId, String status) {
        Optional<MemberOnlineStatus> statusOpt = onlineStatusRepository.findByMemberId(memberId);
        
        if (statusOpt.isPresent()) {
            // 更新現有狀態
            onlineStatusRepository.updateMemberOnlineStatus(memberId, status);
        } else {
            // 創建新的狀態記錄
            MemberOnlineStatus newStatus = new MemberOnlineStatus(memberId, status);
            onlineStatusRepository.save(newStatus);
        }
    }
    
    // 獲取用戶在線狀態
    public String getMemberOnlineStatus(Integer memberId) {
        Optional<MemberOnlineStatus> statusOpt = onlineStatusRepository.findByMemberId(memberId);
        return statusOpt.map(MemberOnlineStatus::getStatus).orElse("offline");
    }
    
    // 獲取最後在線時間
    public LocalDateTime getLastSeenTime(Integer memberId) {
        Optional<MemberOnlineStatus> statusOpt = onlineStatusRepository.findByMemberId(memberId);
        return statusOpt.map(MemberOnlineStatus::getLastSeen).orElse(null);
    }
    
    // 獲取在線用戶列表
    public List<MemberOnlineStatusDTO> getOnlineMembers() {
        List<MemberOnlineStatus> onlineStatuses = onlineStatusRepository.findAllOnlineMembers();
        List<MemberOnlineStatusDTO> statusDTOs = new ArrayList<>();
        
        for (MemberOnlineStatus status : onlineStatuses) {
            MemberOnlineStatusDTO dto = convertToDTO(status);
            statusDTOs.add(dto);
        }
        
        return statusDTOs;
    }
    
    // 獲取聊天室成員的在線狀態
    public List<MemberOnlineStatusDTO> getRoomMembersOnlineStatus(Integer roomId) {
        List<MemberOnlineStatus> statuses = onlineStatusRepository.findOnlineStatusByRoomId(roomId);
        List<MemberOnlineStatusDTO> statusDTOs = new ArrayList<>();
        
        for (MemberOnlineStatus status : statuses) {
            MemberOnlineStatusDTO dto = convertToDTO(status);
            statusDTOs.add(dto);
        }
        
        return statusDTOs;
    }
    
    // 用戶離線處理
    public void handleUserOffline(Integer memberId) {
        updateOnlineStatus(memberId, "offline");
    }
    
    // 批量設置離線狀態（系統維護用）
    public int batchSetOfflineStatus(int minutes) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(minutes);
        return onlineStatusRepository.batchSetOfflineStatus(thresholdTime);
    }
    
    // 獲取在線用戶數量
    public long getOnlineMemberCount() {
        return onlineStatusRepository.countOnlineMembers();
    }
    
    // 轉換為DTO
    private MemberOnlineStatusDTO convertToDTO(MemberOnlineStatus status) {
        MemberOnlineStatusDTO dto = new MemberOnlineStatusDTO();
        dto.setMemberId(status.getMemberId());
        dto.setStatus(status.getStatus());
        dto.setLastSeen(status.getLastSeen());
        dto.setDeviceInfo(status.getDeviceInfo());
        dto.setUpdatedAt(status.getUpdatedAt());
        
        // 設置用戶名稱
        Optional<Member> memberOpt = memberRepository.findById(status.getMemberId());
        if (memberOpt.isPresent()) {
            dto.setMemberName(memberOpt.get().getName());
        }
        
        return dto;
    }
}
