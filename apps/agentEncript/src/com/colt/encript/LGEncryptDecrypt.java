package com.colt.encript;

public class LGEncryptDecrypt {

	public static void main(String[] args) throws Exception{

		String password = null;
		if(args != null && args.length > 0) {
			if(args[0] != null && !"".equals(args[0])) {
				password = args[0].trim();
			}
		}

		if(password != null && !"".equals(password)) {
			String passwordEnc = LGEncryption.encrypt(password);
			System.out.println("Plain Password: " + password);
			System.out.println("Encrypted Password: " + passwordEnc);
		} else {
			System.out.println("Error: password is missing!");
		}
	}
}