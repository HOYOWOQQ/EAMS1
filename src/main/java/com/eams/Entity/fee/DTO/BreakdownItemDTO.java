package com.eams.Entity.fee.DTO;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class BreakdownItemDTO {
    private String label; // 例如：課程類型/課程名稱/年級...
    private Number amount;  // 對應期間「學費收入」加總
}
