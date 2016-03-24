package com.gigaspaces.quality.manyclients.data;

import com.gigaspaces.annotation.pojo.SpaceClass;
import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceVersion;

@SpaceClass
public class ManyClientsMessage {
	
	private String data;
	private String id;
	private int version;
	
	public ManyClientsMessage(){
		
	}
	
	public ManyClientsMessage(String id){
		this.id=id;
	}
	
	@SpaceVersion
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@SpaceId
	public String getId() {
		return id;
	}

	public void randomizeData() {
		this.data = generateRandomString();
	}

	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public static String generateRandomString(){
		return Long.toHexString(Double.doubleToLongBits(Math.random()));
	}
	
}
