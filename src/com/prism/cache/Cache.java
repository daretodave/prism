package com.prism.cache;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.prism.cache.impl.AnimLoader;
import com.prism.cache.impl.FontLoader;
import com.prism.cache.impl.ImageLoader;
import com.prism.cache.impl.StyleLoader;
import com.prism.model.Sequence;
import com.prism.model.Style;

public class Cache {
	
	private static HashMap<String, Loader<?>> loaders = new HashMap<String, Loader<?>>();
	private static HashMap<String, HashMap<String, Object>> cache = new HashMap<String, HashMap<String, Object>>();
	private static HashMap<String, Style>    styles = new HashMap<String, Style>();
	private static HashMap<String, Sequence> anims  = new HashMap<String, Sequence>();
	
	public static <E> E asset(String asset, String assetFolder, Class<E> expected) {
		return expected.cast(cache.get(assetFolder).get(asset));
	}
	
	public static Object asset(String asset, String assetFolder) {
		return cache.get(assetFolder).get(asset);
	}
	
	public static Style style(String style) {
		return styles.get(style);
	}
	
	public static Sequence sequence(String sequence) {
		return anims.get(sequence);
	}
	
	public static BufferedImage image(String asset, String assetFolder) {
		return BufferedImage.class.cast(cache.get(assetFolder).get(asset));
	}
	
	public static void addLoader(Loader<?> loader, String... types) {
		for(String type : types) {
			loaders.put(type.toLowerCase(), loader);
		}
	}
	
	private static void load(String resource, String path, HashMap<String, Object> elements) throws Exception {
		InputStream input = Cache.class.getResourceAsStream("/"+path+resource);
		String extenstion = extenstion(resource);
		String raw 	      = strip(resource);
		Loader<?> loader  = loaders.get(extenstion);
		if(loader == null) {
			throw new Exception("No loader for exenstion ["+extenstion+"]");
		}
		Object element = loader.load(input, raw);
		if(element != null) {
			if(element instanceof Style) {
				styles.put(raw, (Style) element);
			} else if(element instanceof Sequence) {
				anims.put(raw, (Sequence) element);
			} else {	
				elements.put(raw, element);
			}
		}
		input.close();
	}
		
	public static String strip(String name) {
		if(name.lastIndexOf('.') > 0) {
			return  name.substring(0, name.lastIndexOf('.'));
		}
		return "";
	}
	
	public static String extenstion(String name) {
		if(name.lastIndexOf('.') > 0) {
			return  name.replace(name.substring(0, name.lastIndexOf('.')+1), "").toLowerCase();
		}
		return "";
	}
	
	public static void load(String assets) {
		String path = "assets/"+assets+"/";
		HashMap<String, Object> entities = new HashMap<String, Object>();
		cache.put(assets, entities);
		try {
			URL directory = Cache.class.getClassLoader().getResource(path);
			if(directory == null) {
				return;
			}
			switch(directory.getProtocol()) {
			case "file":
				File dir = new File(directory.toURI());
				for(File s : dir.listFiles()) {
					if(!s.isDirectory())
						load(s.getName(), path, entities);
				}
				break;
			case "jar":
				String jarPath = directory.getPath().substring(5, directory.getPath().indexOf("!"));
		        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
		        Enumeration<JarEntry> entries = jar.entries();
		        while(entries.hasMoreElements()) {
		          String name = entries.nextElement().getName();
		          if (name.startsWith(path) && !name.equals(path)) {
		        	  String resource = name.replace(path, "");
		        	  if(!resource.contains("/")) {
		        		  load(resource, path, entities);
		        	  }
		          }
		        }
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static {
		addLoader(new ImageLoader(), "jpg", "png", "jpeg");
		addLoader(new FontLoader(), "ttf", "otf");
		addLoader(new StyleLoader(), "ps");
		addLoader(new AnimLoader(), "anim");
	}

	@SuppressWarnings("unchecked")
	public static <T> T asset(String name, Class<T> type) {
		Set<String> keys = cache.keySet();
		for(String s : keys) {
			Object o = cache.get(s).get(name);
			if(o != null) {
				return (T) o;
			}
		}
		return null;
	}

}
