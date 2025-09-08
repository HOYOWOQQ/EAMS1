package com.eams.Repository.fee;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateNoticeNoService {
	
    @Autowired
    private PaymentNoticeRepository noticeRepo;

    public String generateNextNoticeNo() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "T" + today; // 例如: "T20250729"

        // 步驟1: 從資料庫獲取今天最大的通知單號
        String maxNoticeNo = noticeRepo.findMaxNoticeNoLike(prefix);
        System.out.println("DEBUG: 從資料庫查到的當天最大通知單號: " + maxNoticeNo); // 加入日誌

        int nextSerialNumber = 1; // 預設值為 1，用於當天第一個號碼或處理錯誤時

        if (maxNoticeNo != null) {
            // 檢查返回的號碼是否符合預期格式 (例如 TYYYYMMDD-XXXX)
            // 這裡假設分隔符號是 "-"，且後綴是4位數字
            int hyphenIndex = maxNoticeNo.lastIndexOf('-');
            if (hyphenIndex != -1 && maxNoticeNo.length() > hyphenIndex + 1) {
                try {
                    String currentSerialNumberStr = maxNoticeNo.substring(hyphenIndex + 1);
                    // 額外檢查，確保截取的部分是純數字且長度為4 (防止 T20250729-X 這類非標準格式)
                    if (currentSerialNumberStr.matches("\\d{4}")) {
                        int currentSerialNumber = Integer.parseInt(currentSerialNumberStr);
                        nextSerialNumber = currentSerialNumber + 1;
                        System.out.println("DEBUG: 成功解析並遞增號碼: " + nextSerialNumber); // 加入日誌
                    } else {
                        System.err.println("WARN: 發現非標準的通知單號後綴格式: " + currentSerialNumberStr + ". 將從 0001 開始.");
                        // 如果後綴格式不對，就使用預設的 1
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ERROR: 解析通知單號數字部分時出錯: " + maxNoticeNo + ". 將從 0001 開始. 錯誤訊息: " + e.getMessage());
                    // 如果解析數字出錯，就使用預設的 1
                    e.printStackTrace();
                } catch (StringIndexOutOfBoundsException e) {
                    System.err.println("ERROR: 截取通知單號後綴時索引越界: " + maxNoticeNo + ". 將從 0001 開始. 錯誤訊息: " + e.getMessage());
                    // 如果截取字串出錯，就使用預設的 1
                    e.printStackTrace();
                }
            } else {
                System.err.println("WARN: 從資料庫獲取到的通知單號格式不正確或缺少分隔符號: " + maxNoticeNo + ". 將從 0001 開始.");
                // 如果格式不符合 TYYYYMMDD-XXXX，就使用預設的 1
            }
        } else {
            System.out.println("DEBUG: 資料庫中沒有找到當天現有的通知單號. 將從 0001 開始.");
            // maxNoticeNo 為 null， nextSerialNumber 保持預設值 1
        }

        // 格式化新的通知單號，確保是四位數 (例如 0001, 0002, ...)
        String formattedSerialNumber = String.format("%04d", nextSerialNumber);
        String finalNoticeNo = prefix + "-" + formattedSerialNumber;
        System.out.println("DEBUG: 最終生成的通知單號: " + finalNoticeNo); // 加入日誌
        return finalNoticeNo;
    }
}