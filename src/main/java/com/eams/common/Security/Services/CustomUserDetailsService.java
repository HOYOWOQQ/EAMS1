package com.eams.common.Security.Services;

import com.eams.Entity.member.Member;
import com.eams.Repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先嘗試使用完整關聯查詢
        Member member = null;
        
//        try {
//            // 如果你有 findByAccountWithRolesAndPermissions 方法，使用它
//            member = memberRepository.findByAccountWithRolesAndPermissions(username)
//                    .orElse(null);
//        } catch (Exception e) {
//            // 如果關聯查詢失敗，使用簡單查詢
//            System.out.println("關聯查詢失敗，使用簡單查詢: " + e.getMessage());
//        }
        
        // 備用方案：使用現有的簡單查詢
        
        if (member == null) {
            member = memberRepository.findMemberByAccount(username);
        }
        
        if (member == null) {
            throw new UsernameNotFoundException("用戶不存在: " + username);
        }

//        // 檢查用戶狀態
//        if (!member.getStatus() || !member.getVerified()) {
//            throw new UsernameNotFoundException("用戶已被停用或未驗證: " + username);
//        }

        return CustomUserDetails.create(member);
    }

    @Transactional
    public UserDetails loadUserById(Integer id) {
        Member member = null;
        
       // try {
            // 如果你有 findByIdWithRolesAndPermissions 方法，使用它
         //   member = memberRepository.findByIdWithRolesAndPermissions(id)
        //            .orElse(null);
       // } catch (Exception e) {
            // 備用方案
            member = memberRepository.findById(id).orElse(null);
       // }
        
        if (member == null) {
            throw new UsernameNotFoundException("用戶不存在: " + id);
        }

        return CustomUserDetails.create(member);
    }
}