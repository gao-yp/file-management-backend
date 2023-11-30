package com.gyp.jx.file.management.utils;

import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 读取大图片的分辨率
 * 
 * @author 远方bruce
 * 
 */
@Log4j2
public class ReadResolutionUtil {

	/**
	 * 获取图片的分辨率
	 * @param path 图片地址
	 * @return 信息
	 */
	private static Dimension getImageDim(String path) {
		Dimension result = null;
		String suffix = getFileSuffix(path);
		// 解码具有给定后缀的文件
		Iterator<ImageReader> iterator = ImageIO.getImageReadersBySuffix(suffix);
		if (iterator.hasNext()) {
			ImageReader reader = iterator.next();
			try (ImageInputStream stream = new FileImageInputStream(new File(path))) {
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				result = new Dimension(width, height);
			} catch (IOException e) {
				log.error(e);
			}
		}
		return result;
	}

	/**
	 * 获得图片的后缀名
	 * 
	 * @param path 图片地址
	 * @return 图片后缀
	 */
	private static String getFileSuffix(final String path) {
		String result = null;
		if (path != null) {
			result = "";
			if (path.lastIndexOf('.') != -1) {
				result = path.substring(path.lastIndexOf('.'));
				if (result.startsWith(".")) {
					result = result.substring(1);
				}
			}
		}
		return result;
	}

	/**
	 * 截取Dimension对象获得分辨率
	 * 
	 * @param path 地址
	 * 
	 * @return 分辨率
	 */
	public static Map<Character, Integer> getResolution2(String path) {
		String s = getImageDim(path).toString();
		s = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
		String w = s.substring(s.indexOf("=") + 1, s.indexOf(","));
		String h = s.substring(s.lastIndexOf("=") + 1);
		Map<Character, Integer> map = new HashMap<>();
		map.put('w', Integer.parseInt(w));
		map.put('h', Integer.parseInt(h));
		return map;
	}

}