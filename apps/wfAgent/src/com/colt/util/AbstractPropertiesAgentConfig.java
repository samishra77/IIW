package com.colt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractPropertiesAgentConfig {

	private static Log log = LogFactory.getLog(AbstractPropertiesAgentConfig.class);

	private Properties properties;
	private long lastModification;
	private long lastCheckOnDisk;
	private String overwriteConfigFile;
	private boolean firstTime = true;
	private boolean silent = false;

	abstract protected String getConfigFile();
	/**
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getProperty(String key) throws IOException {
		initProp();
		if(key == null) {
			return null;
		}
		String value = properties.getProperty(key);
		if(value == null) {
			log.error("Property not found for key: " + key);
		}
		return value;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Properties getProperties() throws IOException {
		initProp();
		return properties;
	}

	/**
	 * 
	 * @throws IOException
	 */

	public synchronized void initProp() throws IOException {
		if(overwriteConfigFile != null) {
			initPropOverwrite();
			if(properties != null) {
				return;
			}
		}
		String file = getConfigFile();
		long currentTime = System.currentTimeMillis();
		if(currentTime - this.lastCheckOnDisk > 60000) {
			long fileLastModified = getLastModifiedForResource(file);
			if(fileLastModified > lastModification) {
				properties = null;
				Log log = LogFactory.getLog(this.getClass());
				log.info("Reading config file: " + file);
				lastModification = fileLastModified;
			}
			lastCheckOnDisk = currentTime;
		}

		if(properties == null) {
			properties = new Properties();
			try {
				properties.load(getInputStreamForResource(file));
			} catch(IOException e) {
				Log log = LogFactory.getLog(this.getClass());
				String err = null;
				if(!silent) {
					err = "Couldn't read from file: " + file + ". Will try to get from resource. ";
					//log.info(err + e.toString());
				}

				try {
					properties.load(this.getClass().getResourceAsStream(file));
				} catch(IOException e2) {
					properties = null;
					if(!silent) {
						err = "Couldn't read resource file: " + file;
						log.fatal(err, e2);
					}
					throw new IOException(err);
				}
			}
		}

		if(firstTime) {
			overwriteConfigFile = properties.getProperty("overwrite-config-file");
			firstTime = false;
			if(overwriteConfigFile != null) {
				properties = null;
				lastModification = 0;
				lastCheckOnDisk = 0;
				initProp();
			}
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	private synchronized void initPropOverwrite() throws IOException {
		String file = overwriteConfigFile;
		File fileObj = new File(file);
		long currentTime = System.currentTimeMillis();
		if(currentTime - this.lastCheckOnDisk > 60000) {
			long fileLastModified = fileObj.lastModified();
			if(fileLastModified > lastModification) {
				properties = null;
				Log log = LogFactory.getLog(this.getClass());
				log.info("Reading overwrite config file: " + file);
				lastModification = fileLastModified;
			}
			lastCheckOnDisk = currentTime;
		}

		if(properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
			} catch(IOException e) {
				properties = null;
				if(!silent) {
					String err = "Couldn't read overwrite file: " + file + " ";
					Log log = LogFactory.getLog(this.getClass());
					log.info(err + e.toString());
				}
			}
		}
	}

	public long getLastModification() {
		try {
			initProp();
		} catch (IOException e) {}
		return lastModification;
	}

	/**
	 * @param resourcePath
	 * @param lastModification 0 if file doesn't exist, file.lastModified() otherwise.
	 * @return
	 */
	private long getLastModifiedForResource(String resourcePath) {
		URL fileURL = getClass().getResource(resourcePath);
		if(fileURL != null) {
			File file = new File(fileURL.getPath());
			return file.lastModified();
		}

		return 0;
	}

	/**
	 * 
	 * @param resourcePath
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStreamForResource(String resourcePath) throws IOException {
		//URL fileURL = getClass().getResource("E:/Manju-Testadaptor-28-01-2014/MSP-TestAdapter/src/net/colt/csap/testadaptor/conf/application.properties");
		URL fileURL = getClass().getResource(resourcePath);
		if(fileURL != null) {
			File file = new File(fileURL.getPath());
			return new FileInputStream(file);
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSilent() {
		return silent;
	}

	/**
	 * If silent mode is true, no log with error messages will be generated.
	 * Defaults to false.
	 *
	 * @return
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
}
