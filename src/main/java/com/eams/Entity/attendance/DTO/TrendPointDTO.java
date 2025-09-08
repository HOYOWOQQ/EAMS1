package com.eams.Entity.attendance.DTO;

public class TrendPointDTO {
private String label; // e.g. "1月" / "第1週" / "1"
private int presentCount; // 已出席
private int totalCount; // 總出勤（含出席/缺席/請假）


public TrendPointDTO() {}
public TrendPointDTO(String label, int presentCount, int totalCount) {
this.label = label; this.presentCount = presentCount; this.totalCount = totalCount;
}
public String getLabel() { return label; }
public void setLabel(String label) { this.label = label; }
public int getPresentCount() { return presentCount; }
public void setPresentCount(int presentCount) { this.presentCount = presentCount; }
public int getTotalCount() { return totalCount; }
public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
}