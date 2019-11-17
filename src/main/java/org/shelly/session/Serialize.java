package org.shelly.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * @FileName: Serialize.java
 * @CreateTime: 2019年10月10日上午11:28:22
 * @Description: 序列化工具
 * @Author: Shelly
 */
public class Serialize {

	/**
	 * @Title: serialize
	 * @Description: 序列化
	 * @Param: @param obj
	 * @Param: @return
	 * @Return: byte[]
	 */
	public static byte[] serialize(Object obj) {
		return fstSerialize(obj);
	}

	/**
	 * @Title: deserialize
	 * @Description: 反序列化
	 * @Param: @param clazz
	 * @Param: @param bytes
	 * @Param: @return
	 * @Return: T
	 */
	public static <T> T deserialize(Class<T> clazz, byte[] bytes) {
		return fstDeserialze(clazz, bytes);
	}

	/**
	 * @Title: jdkSerialize
	 * @Description: 使用jdk将对象序列化
	 * @Param: @param obj
	 * @Param: @return
	 * @Return: byte[]
	 */
	public static byte[] jdkSerialize(Object obj) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			byte[] result = baos.toByteArray();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * @Title: jdkDeserialze
	 * @Description: 使用jdk反序列化
	 * @Param: @param clazz
	 * @Param: @param bytes
	 * @Param: @return
	 * @Return: T
	 */
	public static <T> T jdkDeserialze(Class<T> clazz, byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			Object result = ois.readObject();
			return clazz.cast(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * @Title: fstSerialize
	 * @Description: 使用fst将对象序列化
	 * @Param: @param obj
	 * @Param: @return
	 * @Return: byte[]
	 */
	public static byte[] fstSerialize(Object obj) {
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try {
			out = new ByteArrayOutputStream(1024 * 16);
			fout = new FSTObjectOutput(out);
			fout.writeObject(obj);
			fout.flush();
			byte[] result = out.toByteArray();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * @Title: fstDeserialze
	 * @Description: 使用fst反序列化
	 * @Param: @param bytes
	 * @Param: @return
	 * @Return: T
	 */
	public static <T> T fstDeserialze(Class<T> clazz, byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream in = null;
		FSTObjectInput fin = null;
		try {
			in = new ByteArrayInputStream(bytes);
			fin = new FSTObjectInput(in);
			T result = clazz.cast(fin.readObject());
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

}