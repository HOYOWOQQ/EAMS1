package com.eams.common;


public class ApiResponse<T> {
	
	private String message;
    private T data;
    private Long timestamp;
    
	public ApiResponse(String message, T data, Long timestamp) {
		super();
		this.message = message;
		this.data = data;
		this.timestamp = timestamp;
	}
	public ApiResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	// 成功回應
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("操作成功", data, System.currentTimeMillis());
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data, System.currentTimeMillis());
    }
    
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, null, System.currentTimeMillis());
    }
    
    // 錯誤回應
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, System.currentTimeMillis());
    }
    
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(message, data, System.currentTimeMillis());
    }
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}
	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	@Override
	public String toString() {
		return "ApiResponse [message=" + message + ", data=" + data + ", timestamp=" + timestamp + "]";
	}
    
    

}
