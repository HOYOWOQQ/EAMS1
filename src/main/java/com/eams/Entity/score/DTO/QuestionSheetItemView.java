package com.eams.Entity.score.DTO;

import java.math.BigDecimal;

public class QuestionSheetItemView {
    public Integer itemId;
    public Integer seqNo;
    public BigDecimal points;

    public Integer questionId;
    public Integer subjectId;
    public String  subjectName; // 科目中文名（可選）
    public String  qType;       // 對應資料表 q_type
    public String  stem;        // 題幹
    public String  optionsJson; // 如果前端想要預覽選項可用
	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the seqNo
	 */
	public Integer getSeqNo() {
		return seqNo;
	}
	/**
	 * @param seqNo the seqNo to set
	 */
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}
	/**
	 * @return the points
	 */
	public BigDecimal getPoints() {
		return points;
	}
	/**
	 * @param points the points to set
	 */
	public void setPoints(BigDecimal points) {
		this.points = points;
	}
	/**
	 * @return the questionId
	 */
	public Integer getQuestionId() {
		return questionId;
	}
	/**
	 * @param questionId the questionId to set
	 */
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	/**
	 * @return the subjectId
	 */
	public Integer getSubjectId() {
		return subjectId;
	}
	/**
	 * @param subjectId the subjectId to set
	 */
	public void setSubjectId(Integer subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * @return the subjectName
	 */
	public String getSubjectName() {
		return subjectName;
	}
	/**
	 * @param subjectName the subjectName to set
	 */
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	/**
	 * @return the qType
	 */
	public String getqType() {
		return qType;
	}
	/**
	 * @param qType the qType to set
	 */
	public void setqType(String qType) {
		this.qType = qType;
	}
	/**
	 * @return the stem
	 */
	public String getStem() {
		return stem;
	}
	/**
	 * @param stem the stem to set
	 */
	public void setStem(String stem) {
		this.stem = stem;
	}
	/**
	 * @return the optionsJson
	 */
	public String getOptionsJson() {
		return optionsJson;
	}
	/**
	 * @param optionsJson the optionsJson to set
	 */
	public void setOptionsJson(String optionsJson) {
		this.optionsJson = optionsJson;
	}
    
    
}
