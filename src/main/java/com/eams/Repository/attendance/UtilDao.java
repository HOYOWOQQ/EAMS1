package com.eams.Repository.attendance;

public class UtilDao {
	 public static String getLStatusText(String status) {
		 if (status == null) {
		        return "";
		    }
		 switch (status) {
	            case "APPROVED":
	            	return "已審核";
	            case "REJECTED":
	            	return "未通過";
	            case "PENDING":
	            	return "未審核";
	            default:
	            	return "";
	        }
	    }
	 public static String getStatusText(String status) {
		    if (status == null) {
		        return "";
		    }
		    switch (status) {
		        case "ATTEND":
		            return "出席";
		        case "LEAVE":
		            return "請假";
		        case "ABSENT":
		            return "缺席";
		        case "UNMARKED":
		            return "未點名";
		        default:
		            return "未知狀態";
		    }
		}

}
