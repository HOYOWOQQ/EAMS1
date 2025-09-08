package com.eams.Entity.scholarship;

import java.util.List;

import lombok.Data;

@Data
public class GrantTop3BatchRequest {
	 private List<Integer> examPaperIds; // 必填：多張考卷
	    private Boolean dryRun;              // 選填：true=只試算
	    private String titlePrefix;          // 選填：標題前綴，未填用預設

}
