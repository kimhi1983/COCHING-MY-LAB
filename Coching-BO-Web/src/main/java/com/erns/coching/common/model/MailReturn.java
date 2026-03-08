package com.erns.coching.common.model;

public class MailReturn {
	 private Boolean isSuccess = false;
	    private Object returnObject = null;
	    private String errorMessage = "";

	    public MailReturn() {}
	    public MailReturn(Exception ex) {
	        this.SetException(ex);
	    }
	    public MailReturn(Boolean isSuccess, Object retObj, String errMsg) {
	    	this.isSuccess = isSuccess;
	    	this.returnObject = retObj;
	    	this.errorMessage = errMsg;
	    }

	    public void SetException(Exception ex) {
	        this.isSuccess = false;
	        this.returnObject = ex;
	        this.errorMessage = ex.getMessage();
	    }

	    public Boolean getIsSuccess() {
	        return this.isSuccess;
	    }

	    public void isIsSuccess(Boolean isSuccess) {
	        this.isSuccess = isSuccess;
	    }

	    public Object getReturnObject() {
	        return this.returnObject;
	    }

	    public void setReturnObject(Object returnObject) {
	        this.isSuccess = true;
	        this.returnObject = returnObject;
	        this.errorMessage = "";
	    }

	    public String getErrorMessage() {
	        return this.errorMessage;
	    }

	    public void setErrorMessage(String errorMessage) {
	    	this.isSuccess = false;
	        this.returnObject = null;
	        this.errorMessage = errorMessage;
	    }
}
