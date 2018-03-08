package com.taobao.pamirs.cache.config;

import java.util.ArrayList;
import java.util.List;

public class BeanCacheCleanConfig {
	private String beanName;
	private String methodName;
	private String parameterTypes;
	private String cacheCode;
	private String[] cacheCleanCodes;

	public String[] split(String str,char splitChar,char checkChar){
		List<String> result = new ArrayList<String>();			
		String[] list = str.split("\\" +splitChar);
		for(int i=0;i<list.length;i++){
			if(list[i].indexOf(checkChar) >=0 ){
				result.add(list[i]);
			}else{
				result.set(result.size() - 1,result.get(result.size() -1) + splitChar + list[i]);
			}
		}
		return (String[])result.toArray(new String[0]);
	}
	
	public BeanCacheCleanConfig(String str){
		String[] properties = split(str,',','=');
		for(String s:properties){
			String[] nameAndValue = s.split("=");
			nameAndValue[0] = nameAndValue[0].trim();
			nameAndValue[1] = nameAndValue[1].trim();
			if(nameAndValue[0].equals("cacheCleanCodes")){
				this.cacheCleanCodes = nameAndValue[1].split(";");
			}else if(nameAndValue[0].equals("beanName")){
				this.beanName = nameAndValue[1];
			}else if(nameAndValue[0].equals("methodName")){
				this.methodName = nameAndValue[1];
			}else if(nameAndValue[0].equals("parameterTypes")){
				this.parameterTypes = nameAndValue[1];		
			}else{
				throw new RuntimeException("不支持的属性：" + nameAndValue[0]);
			}
		}
		
		/*
		 * 对于4个缓存配置的关键属性.不允许使用默认值.
		 * 
		 * **/
		if(	this.beanName == null
				|| this.methodName == null
				|| this.parameterTypes == null
				|| this.cacheCleanCodes == null
				){
			
			throw new RuntimeException("缓存清空配置信息不足.初始化失败 :" 
					+ beanName + ";"
					+ methodName+";"
					+ parameterTypes + ";");
			
		}
		
		//检查 参数列表是否合理
		if(this.parameterTypes != null){
			int start = this.parameterTypes.indexOf("{");
			int end   = this.parameterTypes.indexOf("}");
			
			
			String subParameterTypes = this.parameterTypes.substring(start+1, end);
			String[] parameterTypesArray = subParameterTypes.split(",");
			
			for (int i = 0; i < parameterTypesArray.length; i++) {
				if( !parameterTypesArray[i].equals("long")
						&& !parameterTypesArray[i].equals("Long")
						&& !parameterTypesArray[i].equals("String")
						&& !parameterTypesArray[i].equals("int")
						&& !parameterTypesArray[i].equals("Integer")
						&& !parameterTypesArray[i].equals("short")
						&& !parameterTypesArray[i].equals("Short")
						&& !parameterTypesArray[i].equals("Object")){
					
					throw new RuntimeException("缓存参数列表配置信息错误,只能使用  String,long,int,short:" 
							+ cacheCode + ";"
							+ parameterTypes + ";");						
				}
			}			
		}
		
		this.cacheCode = BeanCacheConfig.generateCacheCode(this.beanName,this.methodName,this.parameterTypes);

	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("beanName=" + beanName);
		builder.append(",methodName=" + methodName);
		builder.append(",cacheCleanCodes=");
		for(int i =0;i <this.cacheCleanCodes.length;i++){
			if(i >0){
				builder.append("#");
			}
			builder.append(this.cacheCleanCodes[i]);
		}
		return builder.toString();
	}
	public String getBeanName() {
		return beanName;
	}
	public String getMethodName() {
		return methodName;
	}
	public String[] getCacheCleanCodes() {
		return cacheCleanCodes;
	}
	public String getCacheCode() {
		return cacheCode;
	}
    
}
