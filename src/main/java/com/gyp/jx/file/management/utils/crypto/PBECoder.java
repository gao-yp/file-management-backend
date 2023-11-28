package com.gyp.jx.file.management.utils.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * PBE安全编码组件
 * 
 * @author 梁栋
 * @version 1.0
 */
public abstract class PBECoder {
	/**
	 * Java 7 支持以下任意一种算法 PBEWithMD5AndDES PBEWithMD5AndTripleDES
	 * PBEWithSHA1AndDESede PBEWithSHA1AndRC2_40 PBKDF2WithHmacSHA1
	 */
	public static final String ALGORITHM = "PBEWITHMD5andDES";
	/**
	 * 迭代次数
	 */
	public static final int ITERATION_COUNT = 100;

	public static final String DEFAULT_PASSWORD = "gyp";

	/**
	 * 盐初始化&lt;br&gt; 盐长度必须为8字节
	 * 
	 * @return byte[] 盐
	 * @throws Exception
	 */
	public static byte[] initSalt() throws Exception {
		// 实例化安全随机数
		SecureRandom random = new SecureRandom();
		// 产出盐
		return random.generateSeed(8);
	}

	/**
	 * 转换密钥
	 * 
	 * @param password 密码
	 * @return Key 密钥
	 * @throws Exception
	 */
	private static Key toKey(String password) throws Exception {
		// 密钥材料转换
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		// 实例化
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		// 生成密钥
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		return secretKey;
	}

	/**
	 * 加密
	 * 
	 * @param data     数据
	 * @param password 密码
	 * @param salt     盐
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String password, byte[] salt) throws Exception {
		// 转换密钥
		Key key = toKey(password);
		// 实例化PBE参数材料
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
		// 实例化
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 解密
	 * 
	 * @param data     数据
	 * @param password 密码
	 * @param salt     盐
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String password, byte[] salt) throws Exception {
		// 转换密钥
		Key key = toKey(password);
		// 实例化PBE参数材料
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
		// 实例化
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		// 初始化
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * 加密文件
	 * 
	 * @param srcFile 源文件
	 * @param encFile 加密文件
	 * @param pwd     口令
	 * @param salt    盐
	 * @throws Exception
	 */
	public static void encryptFile(File srcFile, File encFile, String pwd, byte[] salt) throws Exception {
		try (InputStream in = new FileInputStream(srcFile);
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				OutputStream out2 = new FileOutputStream(encFile);) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			byte[] encrypt = PBECoder.encrypt(out.toByteArray(), pwd, salt);
			out2.write(encrypt);
		}
	}

	/**
	 * 解密文件
	 * 
	 * @param encFile 加密文件
	 * @param decFile 解密文件
	 * @param pwd     口令
	 * @param salt    盐
	 * @throws Exception
	 */
	public static void decryptFile(File encFile, File decFile, String pwd, byte[] salt) throws Exception {
		try (InputStream in = new BufferedInputStream(new FileInputStream(encFile));
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				OutputStream out2 = new FileOutputStream(decFile);) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			byte[] decrypt = PBECoder.decrypt(out.toByteArray(), pwd, salt);
			out2.write(decrypt);
		}
	}

	public static byte[] decryptFile(File encFile, String pwd, byte[] salt) throws Exception {
		try (InputStream in = new BufferedInputStream(new FileInputStream(encFile));
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			return PBECoder.decrypt(out.toByteArray(), pwd, salt);
		}
	}
	


}
