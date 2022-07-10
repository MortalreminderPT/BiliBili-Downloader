package com.pt.tool;

public class Result<T> {
	private int code;
	private T data;
	private String message;


	public void setCode(int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return code;
	}

	public void setData(T data)
	{
		this.data = data;
	}

	public T getData()
	{
		return data;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}
}
