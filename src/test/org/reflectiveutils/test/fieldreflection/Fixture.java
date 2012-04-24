package org.reflectiveutils.test.fieldreflection;

public class Fixture {
	private String privateField1;
	private String privateField2;
	
	public String publicField1;
	public String publicField2;
	
	public String getPrivateField2() {
		return privateField2;
	}
	public void setPrivateField2(String privateField2) {
		this.privateField2 = privateField2;
	}
	public String getPublicField2() {
		return publicField2;
	}
	public void setPublicField2(String publicField2) {
		this.publicField2 = publicField2;
	}
	
	public Fixture() {
		privateField1 = "privateField1";
		privateField2 = "privateField2";
		publicField1 = "publicField1";
		publicField2 = "publicField2";
	}
	
	
	public static class Fixture2 extends Fixture {}
}


