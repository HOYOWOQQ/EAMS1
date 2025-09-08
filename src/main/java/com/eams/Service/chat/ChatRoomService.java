package com.eams.Service.chat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.eams.Entity.chat.ChatMessage;
import com.eams.Entity.chat.ChatRoom;
import com.eams.Entity.chat.ChatRoomMember;
import com.eams.Entity.chat.chatDTO.ChatRoomCreateDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomMemberDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomUpdateDTO;
import com.eams.Entity.chat.chatDTO.InvitableMemberDTO;
import com.eams.Entity.chat.chatDTO.MemberOnlineStatusDTO;
import com.eams.Entity.course.Course;
import com.eams.Entity.member.Member;
import com.eams.Repository.chat.ChatMessageRepository;
import com.eams.Repository.chat.ChatRoomMemberRepository;
import com.eams.Repository.chat.ChatRoomRepository;
import com.eams.Repository.course.CourseRepository;
import com.eams.Repository.member.MemberRepository;

@Service
@Transactional
public class ChatRoomService {
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private OnlineStatusService onlineStatusService;
    
    // 獲取用戶的聊天室列表
    public List<ChatRoomDTO> getUserChatRooms(Integer userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId);
        List<ChatRoomDTO> roomDTOs = new ArrayList<>();
        
        for (ChatRoom room : chatRooms) {
            ChatRoomDTO dto = convertToDTO(room, userId);
            roomDTOs.add(dto);
        }
        
        return roomDTOs;
    }
    
    // 創建聊天室 - 添加角色權限檢查
    public ChatRoomDTO createChatRoom(ChatRoomCreateDTO createDTO, Integer createdBy) {
        // 檢查創建者權限 - 學生不能創建聊天室
        Optional<Member> creatorOpt = memberRepository.findById(createdBy);
        if (!creatorOpt.isPresent()) {
            throw new RuntimeException("用戶不存在");
        }
        
        Member creator = creatorOpt.get();
        if ("student".equalsIgnoreCase(creator.getRole())) {
            throw new RuntimeException("學生無法創建聊天室，請聯繫老師或管理員");
        }
        
        // 驗證課程是否存在（如果是課程聊天室）
        if ("course".equals(createDTO.getType()) && createDTO.getCourseId() != null) {
            Optional<Course> courseOpt = courseRepository.findById(createDTO.getCourseId());
            if (!courseOpt.isPresent()) {
                throw new RuntimeException("指定的課程不存在");
            }
        }
        
        // 創建聊天室
        ChatRoom chatRoom = new ChatRoom(createDTO.getName(), createDTO.getType(), createdBy);
        chatRoom.setCourseId(createDTO.getCourseId());
        chatRoom.setDescription(createDTO.getDescription());
        chatRoom.setMaxMembers(createDTO.getMaxMembers());
        
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        // 添加創建者為管理員
        ChatRoomMember creatorMember = new ChatRoomMember(savedRoom.getId(), createdBy);
        creatorMember.setRole("admin");
        chatRoomMemberRepository.save(creatorMember);
        
        // 添加其他初始成員
        if (createDTO.getMemberIds() != null && !createDTO.getMemberIds().isEmpty()) {
            for (Integer memberId : createDTO.getMemberIds()) {
                if (!memberId.equals(createdBy)) {
                    ChatRoomMember member = new ChatRoomMember(savedRoom.getId(), memberId);
                    chatRoomMemberRepository.save(member);
                }
            }
        }
        
        // 如果是課程聊天室，自動添加課程相關的師生
//        if ("course".equals(createDTO.getType()) && createDTO.getCourseId() != null) {
//            addCourseMembers(savedRoom.getId(), createDTO.getCourseId(), createdBy);
//        }
        
        return convertToDTO(savedRoom, createdBy);
    }
    
    // 更新聊天室
    public ChatRoomDTO updateChatRoom(Integer roomId, ChatRoomUpdateDTO updateDTO, Integer userId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            throw new RuntimeException("聊天室不存在");
        }
        
        ChatRoom room = roomOpt.get();
        
        // 檢查權限（只有創建者或管理員可以更新）
        if (!canUserManageRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限編輯此聊天室");
        }
        
        // 更新聊天室信息
        if (updateDTO.getName() != null) {
            room.setName(updateDTO.getName());
        }
        if (updateDTO.getDescription() != null) {
            room.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getMaxMembers() != null) {
            room.setMaxMembers(updateDTO.getMaxMembers());
        }
        
        ChatRoom updatedRoom = chatRoomRepository.save(room);
        return convertToDTO(updatedRoom, userId);
        
    }
    
    // 刪除聊天室
    public boolean deleteChatRoom(Integer roomId, Integer userId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
            return false;
        }
        
        ChatRoom room = roomOpt.get();
        
        // 檢查權限（只有創建者可以刪除）
        if (!room.getCreatedBy().equals(userId)) {
            throw new RuntimeException("只有聊天室創建者可以刪除聊天室");
        }
        
        // 軟刪除聊天室
        room.setIsActive(false);
        chatRoomRepository.save(room);
        
        return true;
    }
    
    // 獲取聊天室詳情
    public ChatRoomDTO getChatRoomDetail(Integer roomId, Integer userId) {
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (!roomOpt.isPresent() || !roomOpt.get().getIsActive()) {
            return null;
        }
        
        ChatRoom room = roomOpt.get();
        
        // 檢查權限
        if (!chatRoomRepository.canUserAccessRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限訪問此聊天室");
        }
        
        return convertToDTO(room, userId);
    }
    
    // 獲取聊天室成員列表
    public List<ChatRoomMemberDTO> getChatRoomMembers(Integer roomId, Integer userId) {
        // 檢查權限
        if (!chatRoomRepository.canUserAccessRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限查看此聊天室的成員");
        }
        
        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoomIdAndIsActiveTrueOrderByRole(roomId);
        List<ChatRoomMemberDTO> memberDTOs = new ArrayList<>();
        
        for (ChatRoomMember member : members) {
            ChatRoomMemberDTO dto = convertMemberToDTO(member);
            memberDTOs.add(dto);
        }
        
        return memberDTOs;
    }
    
    // 邀請成員到聊天室 - 增強的邀請功能
    public boolean inviteMemberToRoom(Integer roomId, Integer memberId, Integer operatorId) {
        // 檢查操作權限 - 只有創建者或管理員可以邀請
        if (!canUserManageRoom(roomId, operatorId)) {
            throw new RuntimeException("您沒有權限邀請成員");
        }
        
        // 檢查被邀請者是否存在
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("被邀請的用戶不存在");
        }
        
        // 檢查是否已經是成員
        if (chatRoomMemberRepository.existsByRoomIdAndMemberIdAndIsActiveTrue(roomId, memberId)) {
            throw new RuntimeException("該用戶已經是聊天室成員");
        }
        
        // 檢查聊天室容量
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            ChatRoom room = roomOpt.get();
            long currentMemberCount = chatRoomMemberRepository.countByRoomIdAndIsActiveTrue(roomId);
            if (currentMemberCount >= room.getMaxMembers()) {
                throw new RuntimeException("聊天室已滿，無法邀請新成員");
            }
        }
        
        // 添加成員
        ChatRoomMember newMember = new ChatRoomMember(roomId, memberId);
        chatRoomMemberRepository.save(newMember);
        
        return true;
    }
    
    // 添加成員到聊天室
    public boolean addMemberToRoom(Integer roomId, Integer memberId, Integer operatorId) {
        return inviteMemberToRoom(roomId, memberId, operatorId);
    }
    
    // 移除聊天室成員
    public boolean removeMemberFromRoom(Integer roomId, Integer memberId, Integer operatorId) {
    	System.out.println("operatorId: " + operatorId + ", memberId: " + memberId);
        // 檢查操作權限
        if (!canUserManageRoom(roomId, operatorId) && !operatorId.equals(memberId)) {
            throw new RuntimeException("您沒有權限移除成員");
        }
        
        // 不能移除聊天室創建者
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (roomOpt.isPresent() && roomOpt.get().getCreatedBy().equals(memberId)) {
            throw new RuntimeException("無法移除聊天室創建者");
        }
        
        int result = chatRoomMemberRepository.removeMemberFromRoom(roomId, memberId);
        return result > 0;
    }
    
    // 搜索聊天室
    public List<ChatRoomDTO> searchChatRooms(String keyword, Integer userId) {
        List<ChatRoom> rooms = chatRoomRepository.searchChatRooms(keyword, userId);
        List<ChatRoomDTO> roomDTOs = new ArrayList<>();
        
        for (ChatRoom room : rooms) {
            ChatRoomDTO dto = convertToDTO(room, userId);
            roomDTOs.add(dto);
        }
        
        return roomDTOs;
    }
    
    // 獲取可邀請的成員列表（排除已在聊天室的成員）
    public List<InvitableMemberDTO> getInvitableMembers(Integer roomId, Integer userId) {
        // 檢查權限
        if (!canUserManageRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限查看可邀請成員列表");
        }
        
        // 獲取所有成員
        List<Member> allMembers = memberRepository.findAll();
        
        // 獲取聊天室現有成員ID
        List<ChatRoomMember> existingMembers = chatRoomMemberRepository.findByRoomIdAndIsActiveTrueOrderByRole(roomId);
        List<Integer> existingMemberIds = existingMembers.stream()
                .map(ChatRoomMember::getMemberId)
                .toList();
        
        // 獲取聊天室信息（用於推薦相關成員）
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        Integer courseId = roomOpt.map(ChatRoom::getCourseId).orElse(null);
        
        // 過濾出可邀請的成員並轉換為DTO
        List<InvitableMemberDTO> invitableMembers = new ArrayList<>();
        for (Member member : allMembers) {
            if (!existingMemberIds.contains(member.getId())) {
                InvitableMemberDTO dto = convertToInvitableMemberDTO(member, courseId);
                invitableMembers.add(dto);
            }
        }
        
        // 排序：推薦成員在前，在線成員在前，按角色排序
        invitableMembers.sort((a, b) -> {
            // 首先按推薦狀態排序
            if (!a.getIsRecommended().equals(b.getIsRecommended())) {
                return b.getIsRecommended().compareTo(a.getIsRecommended());
            }
            // 然後按在線狀態排序
            if (!a.isOnline() && b.isOnline()) return 1;
            if (a.isOnline() && !b.isOnline()) return -1;
            // 最後按角色排序（教師 > 管理員 > 助教 > 學生）
            return getRolePriority(a.getRole()) - getRolePriority(b.getRole());
        });
        
        return invitableMembers;
    }
    
    // 轉換Member為InvitableMemberDTO
    private InvitableMemberDTO convertToInvitableMemberDTO(Member member, Integer courseId) {
        InvitableMemberDTO dto = new InvitableMemberDTO(member.getId(), member.getName(), member.getRole());
        dto.setEmail(member.getEmail());
        
        // 設置在線狀態
        try {
            String onlineStatus = onlineStatusService.getMemberOnlineStatus(member.getId());
            dto.setOnlineStatus(onlineStatus);
            dto.setLastSeen(onlineStatusService.getLastSeenTime(member.getId()));
        } catch (Exception e) {
            dto.setOnlineStatus("offline");
        }
        
        // 如果是課程聊天室，標記相關成員為推薦
        if (courseId != null) {
            // 這裡可以根據課程選課表來判斷是否為推薦成員
            // 例如：同課程的師生
            // dto.setIsRecommended(courseEnrollRepository.existsByCourseIdAndStudentId(courseId, member.getId()));
            
            // 暫時的邏輯：教師和管理員標記為推薦
            if ("teacher".equalsIgnoreCase(member.getRole()) || "admin".equalsIgnoreCase(member.getRole())) {
                dto.setIsRecommended(true);
            }
        }
        
        return dto;
    }
    
    // 獲取角色優先級（用於排序）
    private int getRolePriority(String role) {
        switch (role.toLowerCase()) {
            case "admin": return 1;
            case "teacher": return 2;
            case "assistant": return 3;
            case "student": return 4;
            default: return 5;
        }
    }
    
    // 檢查用戶是否可以管理聊天室
    private boolean canUserManageRoom(Integer roomId, Integer userId) {
    	
        Optional<ChatRoom> roomOpt = chatRoomRepository.findById(roomId);
        if (!roomOpt.isPresent()) {
        	System.out.println("room createdBy: " + roomOpt.get().getCreatedBy());
            return false;
        }
        
        ChatRoom room = roomOpt.get();
        // 創建者或管理員可以管理
        return room.getCreatedBy().equals(userId) || 
               chatRoomRepository.isUserRoomAdmin(roomId, userId);
    }
    
    // 為課程聊天室添加相關成員
//    private void addCourseMembers(Integer roomId, Integer courseId, Integer createdBy) {
//        try {
//            // 這裡可以根據課程獲取相關的師生並添加到聊天室
//            // 具體實現需要根據你的課程選課表結構來調整
//            
//            // 示例：獲取課程的所有學生
//            // List<CourseEnroll> enrollments = courseEnrollRepository.findByCourseIdAndStatus(courseId, "enrolled");
//            // for (CourseEnroll enrollment : enrollments) {
//            //     if (!enrollment.getStudent().getId().equals(createdBy)) {
//            //         ChatRoomMember member = new ChatRoomMember(roomId, enrollment.getStudent().getId());
//            //         chatRoomMemberRepository.save(member);
//            //     }
//            // }
//            
//            System.out.println("自動添加課程相關成員到聊天室: " + roomId);
//        } catch (Exception e) {
//            System.err.println("添加課程成員時發生錯誤: " + e.getMessage());
//        }
//    }
    
    // 轉換為DTO
    private ChatRoomDTO convertToDTO(ChatRoom room, Integer currentUserId) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setType(room.getType());
        dto.setCourseId(room.getCourseId());
        dto.setDescription(room.getDescription());
        dto.setCreatedBy(room.getCreatedBy());
        dto.setMaxMembers(room.getMaxMembers());
        dto.setIsActive(room.getIsActive());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());
        dto.setLastMessageAt(room.getLastMessageAt());
        
        // 設置課程名稱
        if (room.getCourseId() != null) {
            Optional<Course> courseOpt = courseRepository.findById(room.getCourseId());
            if (courseOpt.isPresent()) {
                dto.setCourseName(courseOpt.get().getName());
            }
        }
        
        // 設置創建者名稱
        Optional<Member> creatorOpt = memberRepository.findById(room.getCreatedBy());
        if (creatorOpt.isPresent()) {
            dto.setCreatorName(creatorOpt.get().getName());
        }
        
        // 設置成員數量
        long memberCount = chatRoomMemberRepository.countByRoomIdAndIsActiveTrue(room.getId());
        dto.setCurrentMembers((int) memberCount);
        
        // 設置未讀消息數量
        long unreadCount = chatMessageRepository.countUnreadMessagesByRoomIdAndUserId(room.getId(), currentUserId);
        dto.setUnreadCount((int) unreadCount);
        
        // 設置用戶在此聊天室的狀態
        Optional<ChatRoomMember> memberOpt = chatRoomMemberRepository.findByRoomIdAndMemberIdAndIsActiveTrue(room.getId(), currentUserId);
        if (memberOpt.isPresent()) {
            ChatRoomMember member = memberOpt.get();
            dto.setIsMember(true);
            dto.setMemberRole(member.getRole());
            dto.setIsMuted(member.getIsMuted());
            // 設置是否可以管理（邀請其他人）
            dto.setCanManage(canUserManageRoom(room.getId(), currentUserId));
        } else {
            dto.setIsMember(false);
            dto.setCanManage(false);
        }
        
        // 計算在線成員數量
        try {
            List<MemberOnlineStatusDTO> onlineStatuses = onlineStatusService.getRoomMembersOnlineStatus(room.getId());
            long onlineCount = onlineStatuses.stream()
                    .filter(status -> !"offline".equals(status.getStatus()))
                    .count();
            dto.setOnlineMembers((int) onlineCount);
        } catch (Exception e) {
            dto.setOnlineMembers(0);
        }
        
        // 設置最後一條消息信息
        Pageable pageable = PageRequest.of(0, 1);
        List<ChatMessage> result = chatMessageRepository.findLastMessageByRoomId(room.getId(), pageable);
        Optional<ChatMessage> lastMessageOpt = result.stream().findFirst();
        if (lastMessageOpt.isPresent()) {
            ChatMessage lastMessage = lastMessageOpt.get();
            dto.setLastMessageContent(lastMessage.getContent());
            
            Optional<Member> senderOpt = memberRepository.findById(lastMessage.getSenderId());
            if (senderOpt.isPresent()) {
                dto.setLastMessageSenderName(senderOpt.get().getName());
            }
        }
        
        return dto;
    }
    
    // 轉換成員為DTO
    private ChatRoomMemberDTO convertMemberToDTO(ChatRoomMember member) {
        ChatRoomMemberDTO dto = new ChatRoomMemberDTO();
        dto.setId(member.getId());
        dto.setRoomId(member.getRoomId());
        dto.setMemberId(member.getMemberId());
        dto.setRoomRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());
        dto.setLastReadAt(member.getLastReadAt());
        dto.setIsMuted(member.getIsMuted());
        dto.setIsActive(member.getIsActive());
        
        // 設置成員基本信息
        Optional<Member> memberInfoOpt = memberRepository.findById(member.getMemberId());
        if (memberInfoOpt.isPresent()) {
            Member memberInfo = memberInfoOpt.get();
            dto.setMemberName(memberInfo.getName());
            dto.setMemberRole(memberInfo.getRole());
        }
        
        // 設置在線狀態
        try {
            String onlineStatus = onlineStatusService.getMemberOnlineStatus(member.getMemberId());
            dto.setOnlineStatus(onlineStatus);
            dto.setLastSeen(onlineStatusService.getLastSeenTime(member.getMemberId()));
        } catch (Exception e) {
            dto.setOnlineStatus("offline");
        }
        
        return dto;
    }
    
}