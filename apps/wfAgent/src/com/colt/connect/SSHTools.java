package com.colt.connect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHTools {

	private Log log;
	private String serverIP;
	private String username;
	private String password;
	private String localPath;
	private String keyPath;
	private boolean useJSch = true;
	private int commandTimeout = -1;
	private final int CONNECTTIMEOUT = 15000;  //in miliseconds
	private Session session=null;

	public Session getSshSession() throws Exception {
		try {
			JSch jsch = new JSch();
			this.session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			java.util.Properties config = new java.util.Properties();
        	config.put("StrictHostKeyChecking", "no");
        	session.setConfig(config);
			session.setServerAliveCountMax(0);
			if (commandTimeout > 0) {
				session.setTimeout(commandTimeout);
			}

			return session;

		} catch (Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Default constructor;
	 */
	public SSHTools() {
		log = LogFactory.getLog(this.getClass());
	}

	/**
	 *
	 * @param localPath
	 */
	public SSHTools(String localPath, boolean useJSch) {
		log = LogFactory.getLog(this.getClass());
		this.localPath = localPath;
		this.useJSch = useJSch;
	}

	/**
	 * useJSch default = true.
	 * @param serverIP
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	public SSHTools(String serverIP, String username, String password) throws Exception {
		log = LogFactory.getLog(this.getClass());
		this.serverIP = serverIP;
		this.username = username;
		this.password = password;
	}

	/**
	 *
	 * @param serverIP
	 * @param username
	 * @param password
	 * @param localPath
	 * @param useJSch
	 * @throws Exception
	 */
	public SSHTools(String serverIP, String username, String password, String localPath, boolean useJSch) throws Exception {
		log = LogFactory.getLog(this.getClass());
		this.serverIP = serverIP;
		this.username = username;
		this.password = password;
		this.localPath = localPath;
		this.useJSch = useJSch;
	}

	/**
	 *
	 * @param serverIP
	 * @param username
	 * @param password
	 * @param localPath
	 * @param useJSch
	 * @throws Exception
	 */
	public SSHTools(String serverIP, String username, String password, String localPath, String keyPath, boolean useJSch) throws Exception {
		log = LogFactory.getLog(this.getClass());
		this.serverIP  = serverIP;
		this.username  = username;
		this.password  = password;
		this.localPath = localPath;
		this.useJSch   = useJSch;
		if (keyPath == null || "".equals(keyPath)) {
			this.keyPath = "default";
		} else {
			this.keyPath   = keyPath;
		}
	}

	/**
	 * Execute command on remote address.
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public String execute(String command) throws Exception {
		try {
			JSch jsch = new JSch();
			this.readPrivateKey(jsch);

			Session session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			session.connect(CONNECTTIMEOUT);
			session.setServerAliveCountMax(0);
			if (commandTimeout > 0) {
				session.setTimeout(commandTimeout);
			}
			Channel channel = session.openChannel("exec");
			InputStream resultStream = channel.getInputStream();
			InputStream errStream = ((ChannelExec)channel).getErrStream();

			((ChannelExec)channel).setCommand(command);
			((ChannelExec)channel).connect();

			StringBuffer sbReturn = new StringBuffer();
			StringBuffer sbErrReturn = new StringBuffer();

			int exitStatus = 0;

			byte[] tmp = new byte[1024];
			byte[] errtmp = new byte[1024];

			while (true) {
				while (resultStream.available() > 0) {
					int i = resultStream.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					sbReturn.append(new String(tmp, 0, i));
				}
				while (errStream.available() > 0) {
					int j = errStream.read(errtmp, 0, 1024);
					if (j < 0) {
						break;
					}
					sbErrReturn.append(new String(errtmp, 0, j));
				}
				if (channel.isClosed()) {
					while (resultStream.available() > 0) {
						int i = resultStream.read(tmp, 0, 1024);
						if (i < 0) {
							break;
						}
						sbReturn.append(new String(tmp, 0, i));
					}
					while (errStream.available() > 0) {
						int j = errStream.read(errtmp, 0, 1024);
						if (j < 0) {
							break;
						}
						sbErrReturn.append(new String(errtmp, 0, j));
					}
					exitStatus = channel.getExitStatus();
					break;
				}
				Thread.sleep(50);
			}
			channel.disconnect();
			session.disconnect();

			if (exitStatus != 0) {
				throw new Exception(sbReturn.toString() + sbErrReturn.toString());
			}

			return sbReturn.toString() + sbErrReturn.toString();
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Execute command on remote address.
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public String executeShell(String command) throws Exception {
		try {
			final int sleepInterval = 200;  // each sleep interval is 0.2 second
			final int sleepCountMax = 100;  // number of sleep intervals in order to reach 20 seconds, which is the "idle" timeout

			JSch jsch = new JSch();

			Session session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			session.connect(CONNECTTIMEOUT);
			session.setServerAliveCountMax(0);
			if (commandTimeout > 0) {
				session.setTimeout(commandTimeout);
			}
			Channel channel = session.openChannel("shell");
			InputStream resultStream = channel.getInputStream();

			PrintStream out = new PrintStream(channel.getOutputStream());

			((ChannelShell)channel).setPtyType("vt100",280, 50, 640, 480);


			((ChannelShell)channel).connect();

			out.println(command);
			out.flush();

			StringBuffer sbReturn = new StringBuffer();

			int exitStatus = 0;

			byte[] tmp = new byte[1024];

			int sleepcount = 0;
			while (true) {
				while (resultStream.available() > 0) {
					int i = resultStream.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					sbReturn.append(new String(tmp, 0, i));
					sleepcount = 0;
				}
				if (channel.isClosed()) {
					while (resultStream.available() > 0) {
						int i = resultStream.read(tmp, 0, 1024);
						if (i < 0) {
							break;
						}
						sbReturn.append(new String(tmp, 0, i));
					}
					exitStatus = channel.getExitStatus();
					break;
				}
				Thread.sleep(sleepInterval);
				sleepcount++;
				if (sleepcount>sleepCountMax) {
					break;
				}
			}
			channel.disconnect();
			session.disconnect();

			if (exitStatus != 0) {
				throw new Exception(sbReturn.toString());
			}

			return sbReturn.toString();
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Creates file on remote address. If fileContent is null or it's lenght is 0, no remote
     * file will be created.
     *
	 * @param filePath
	 * @param fileName
	 * @param fileContent
	 * @throws Exception
	 */
	public void createFile(String filePath, String fileName, StringBuilder fileContent) throws Exception {
		try {
			if ( ! useJSch ) {
				this.createLocalFile(filePath, fileName, fileContent);

			} else {
				this.createRemoteFile(filePath, fileName, fileContent);
			}

		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * Copies a file from remote to local.
	 * @param filePath
	 * @param fileName
	 * @param destPath
	 * @throws Exception
	 */
	public void copyRemoteFile(String filePath, String fileName, String destPath) throws Exception {
		FileOutputStream fos = null;
		Session session = null;
		try {
			File d = new File( destPath );
			if ( ! d.exists() ) {
				d.mkdirs(); //Creates the directories, if its doesn't exist
			}
			if (!filePath.endsWith("/")) {
				filePath += "/";
			}
			if (!destPath.endsWith("/")) {
				destPath += "/";
			}
			JSch jsch = new JSch();
			this.readPrivateKey(jsch);
		    session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			session.connect(CONNECTTIMEOUT);

			String command = "scp -p -f " + filePath + fileName;

			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);

			InputStream in = channel.getInputStream();
			OutputStream out = channel.getOutputStream();

			channel.connect();

			byte[] buf = new byte[1024];

			// send '\0'
			buf[0] = 0;
			out.write(buf, 0, 1);
			out.flush();

			while (true) {

				int c = checkAck(in);
				if (c != 'T') {
					break;
				}

				long modtime = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					modtime = modtime * 10L + (buf[0] - '0');
				}
				in.read(buf, 0, 2);
				long acctime = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					acctime = acctime * 10L + (buf[0] - '0');
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				while (true) {
					c = checkAck(in);
					if (c == 'C') {
						break;
					}
				}
				in.read(buf, 0, 5);
				long filesize = 0L;
				while (true) {
					if (in.read(buf, 0, 1) < 0) {
						// error
						break;
					}
					if (buf[0] == ' ') {
						break;
					}
					filesize = filesize * 10L + (buf[0] - '0');
				}

				String file = null;
				for (int i = 0;; i++) {
					in.read(buf, i, 1);
					if (buf[i] == (byte) 0x0a) {
						file = new String(buf, 0, i);
						break;
					}
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();

				// read a content of lfile
				fos = new FileOutputStream(destPath + file);
				int foo;
				while (true) {
					if (buf.length < filesize) {
						foo = buf.length;
					} else {
						foo = (int) filesize;
					}
					foo = in.read(buf, 0, foo);
					if (foo < 0) {
						// error
						break;
					}
					fos.write(buf, 0, foo);
					filesize -= foo;
					if (filesize == 0L) {
						break;
					}
				}
				fos.close();
				fos = null;
				File tempfile = new File(destPath + file);
				tempfile.setLastModified(modtime * 1000);

				if (checkAck(in) != 0) {
					break;
				}

				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
			}

			session.disconnect();
		} catch (Exception e) {
			log.debug(e.getMessage());
			try{if(fos!=null) {
				fos.close();
			}}catch(Exception ex){}
			throw e;
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * Copies a file from local to remote.
	 * @param filePath
	 * @param fileName
	 * @param destPath
	 * @throws Exception
	 */
	public void copyLocalFileTo(String filePath, String fileName, String destPath) throws Exception {
		FileInputStream fis=null;
		Session session = null;
		try {
			JSch jsch = new JSch();
			this.readPrivateKey(jsch);
			session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			session.connect(CONNECTTIMEOUT);

			// exec 'scp -t rfile' remotely
			String command="scp -p -t " + destPath + "/" + fileName;
			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);

			// get I/O streams for remote scp
			OutputStream out=channel.getOutputStream();
			InputStream in=channel.getInputStream();
			channel.connect();

			while(true) {
				if(checkAck(in)!=0){
					break;
				}

				// send "C0644 filesize filename", where filename should not include '/'
				long filesize = (new File(filePath+"/"+fileName)).length();
				command="C0644 "+filesize+" ";
				if(fileName.lastIndexOf('/')>0) {
					command+= fileName.substring(fileName.lastIndexOf('/')+1);
				} else {
					command+= fileName;
				}
				command+= "\n";
				out.write(command.getBytes());
				out.flush();
				if(checkAck(in) != 0) {
					break;
				}

				// send a content of fileName
				fis = new FileInputStream(filePath+"/"+fileName);
				byte[] buf=new byte[1024];
				while(true){
					int len = fis.read(buf, 0, buf.length);
					if(len <= 0) {
						break;
					}
					out.write(buf, 0, len); //out.flush();
				}
				fis.close();
				fis = null;
				// send '\0'
				buf[0] = 0;
				out.write(buf, 0, 1);
				out.flush();
				if(checkAck(in)!=0){
					break;
				}
				out.close();
				channel.disconnect();
				session.disconnect();
				break;
			}
		} catch(Exception e) {
			log.debug(e.getMessage());
			try {
				if(fis != null) {
					fis.close();
				}
			} catch(Exception ee) {
			}
			throw e;
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * @return Returns the localPath.
	 */
	public String getLocalPath() {
		return localPath;
	}

	/**
	 * @param localPath The localPath to set.
	 */
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	/**
	 * @return Returns the useJSch.
	 */
	public boolean isUseJSch() {
		return useJSch;
	}

	/**
	 * @param useJSch The useJSch to set.
	 */
	public void setUseJSch(boolean useJSch) {
		this.useJSch = useJSch;
	}

	/**
	 * @return Returns the keyPath.
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * @param keyPath The keyPath to set.
	 */
	public void setKeyPath(String keyPath) {
		this.keyPath = keyPath;
	}

	/* ***************** Private Methods ***************** */

	/**
	 * Creates file directly on remote address.
	 *
	 * @param filePath
	 * @param fileName
	 * @param fileContent
	 * @throws Exception
	 */
	private void createRemoteFile( String filePath, String fileName, StringBuilder fileContent ) throws Exception {
		Session session = null;
		try {
			if (fileContent != null && fileContent.length() > 0) {
				int fileSize = 0;
				int count = 0;

				JSch jsch = new JSch();
				this.readPrivateKey(jsch);
				session = jsch.getSession(username, serverIP, 22);

				// username and password will be given via UserInfo interface.
				MyUserInfo ui = new MyUserInfo();
				ui.setPassword(password);
				session.setUserInfo(ui);
				session.connect(CONNECTTIMEOUT);

				while( fileSize <= 0 && count < 10 ){
					// exec 'scp -t rfile' remotely
					String command = "scp -p -t " + filePath + fileName;

					ChannelExec channel = (ChannelExec) session.openChannel("exec");
					InputStream inputRemote = channel.getInputStream();
					OutputStream outRemote = channel.getOutputStream();
					channel.setCommand(command);
					channel.connect();

					byte[] bufConfig = fileContent.toString().getBytes();

					// send "C0644 filesize filename", where filename should not
					// include '/'
					int filesize = bufConfig.length;
					command = "C0644 " + filesize + " ";
					command += fileName + "\n";
					outRemote.write(command.getBytes());
					outRemote.flush();

					checkAck(inputRemote);

					// send a content of configuration into a remote file
					byte[] buf = new byte[1024];
					int len = bufConfig.length / buf.length;
					int off = 0;
					for (int l = 0; l < len; l++) {
						outRemote.write(bufConfig, off, buf.length);
						outRemote.flush();
						off += buf.length;
					}
					int mod = bufConfig.length % buf.length;
					if (mod > 0) {
						outRemote.write(bufConfig, off, mod);
						outRemote.flush();
					}

					byte[] eof = new byte[] { 0 };
					outRemote.write(eof, 0, 1); // EOF
					outRemote.flush();

					outRemote.close();

					checkAck(inputRemote);

					// Assert size of file > 0
					String lsCommand = "cat " + filePath + fileName + " | wc -c";
					ChannelExec lsChannel = (ChannelExec) session.openChannel("exec");
					InputStream lsInputRemote = lsChannel.getInputStream();
					lsChannel.setCommand( lsCommand );
					lsChannel.connect();

					StringBuilder ret = new StringBuilder();
					int c;
					while ( (c = lsInputRemote.read() ) != -1) {
						ret.append( (char) c );
					}
					try{
						fileSize = Integer.parseInt( ret.toString().trim() );
					}catch( NumberFormatException nfe ){}
					count ++;
				}

				session.disconnect();

				if ( fileSize <= 0 ) {
					throw new Exception("Error while creating file on remote host!");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}


	/**
	 * Creates the file local, and then send it to remote address.
	 * @param filePath
	 * @param fileName
	 * @param fileContent
	 * @throws Exception
	 */
	private void createLocalFile(String filePath, String fileName, StringBuilder fileContent) throws Exception {
		try {
			if (fileContent != null && fileContent.length() > 0) {
				fileName = localPath + fileName;

				File d = new File( localPath );
				if ( ! d.exists() ) {
					d.mkdir(); //Creates the directory, if it doesn't exist
				}

				File f = new File(fileName);
	            FileOutputStream out = new FileOutputStream(f);
	            out.write(fileContent.toString().getBytes());
	            out.flush();
	            out.close();

	            Process myProcess = Runtime.getRuntime().exec(
						"/usr/bin/scp " + fileName + " " + username+"@"+serverIP+":"+filePath);

				myProcess.waitFor();

				//Deletes file
				f.delete();

				if (myProcess.exitValue() != 0) {
					InputStream errors = myProcess.getErrorStream();
					String errorStr = "";
					for (int i = 1; i <= errors.available(); i++) {
						errorStr += (char)errors.read();
					}
					throw new Exception("Failed: " + errorStr);
				}

			}
		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}

	private void readPrivateKey(JSch jsch) {

		if (keyPath == null || keyPath.equals("default")) {
			try {
				jsch.addIdentity(System.getenv("HOME") + "/.ssh/id_rsa");
			} catch (JSchException e) {

				try {
					jsch.addIdentity(System.getenv("HOME") + "/.ssh/id_dsa");
				}  catch (JSchException ee) {
					log.debug(ee.getMessage());
				}

				log.debug(e.getMessage());
			}
		}
		else {
			try {
				jsch.addIdentity(this.keyPath);
			} catch (JSchException e) {
				log.debug(e.getMessage());
			}
		}
	}

	/**
	 *
	 * @param InputStream
	 * @return
	 * @throws Exception
	 */
	private int checkAck(InputStream in) throws Exception {
		int b = in.read();
		// b may be 0 for success,
		// 1 for error,
		// 2 for fatal error,
		// -1
		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			// error or fatal error
			throw new Exception(sb.toString());
		}
		return b;
	}

	/**
	 *
	 * @author Intelinet
	 */
	private class MyUserInfo implements UserInfo {
		private String passwd;

		public String getPassword() {
			return passwd;
		}

		public void setPassword(String pass) {
			passwd = pass;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
		}
	}

	/**
	 * Returns the specified timeout utilized during the execution
	 * of a command.
	 * @return The timeout. The value -1 indicates no timeout.
	 */
	public int getCommandTimeout() {
		return commandTimeout;
	}

	/**
	 * Set the timeout that must be used during the execution of a
	 * command. If the connection stays idle for a time longer than
	 * the specified by this method, the connection is closed and the
	 * method execute() returns.
	 * The value -1 indicates no timeout.
	 * @param commandTimeout
	 */
	public void setCommandTimeout(int commandTimeout) {
		this.commandTimeout = commandTimeout;
	}

	/**
	 * Disable the timeout of command executions. The same as
	 * setCommandTimeout(-1)
	 *
	 */
	public void clearCommandTimeout() {
		this.commandTimeout = -1;
	}

	/**
	 * @author Aricent
	 * Method to get Session, InputStream, PrintStream
	 *
	 */
	public MySSHInfo sshInfo() throws Exception {
		try {
			JSch jsch = new JSch();
			this.readPrivateKey(jsch);

			Session session = jsch.getSession(username, serverIP, 22);

			// username and password will be given via UserInfo interface.
			MyUserInfo ui = new MyUserInfo();
			ui.setPassword(password);
			session.setUserInfo(ui);
			java.util.Properties config = new java.util.Properties();
        	config.put("StrictHostKeyChecking", "no");
        	session.setConfig(config);
			session.connect(CONNECTTIMEOUT);
			session.setServerAliveCountMax(0);
			if (commandTimeout > 0) {
				session.setTimeout(commandTimeout);
			}
			Channel channel = session.openChannel("shell");
			InputStream in = channel.getInputStream();

			PrintStream out = new PrintStream(channel.getOutputStream());

			((ChannelShell)channel).setPtyType("vt100",280, 50, 640, 480);


			((ChannelShell)channel).connect();

			MySSHInfo sshInfo = new MySSHInfo();
			sshInfo.setSession(session);
			sshInfo.setIn(in);
			sshInfo.setOut(out);
			return sshInfo;

		} catch (Exception e) {
			log.debug(e.getMessage());
			throw e;
		}
	}

	/**
	 * @author Aricent
	 * Class used in ConnectSSH to get SSH fields Information
	 *
	 */
	public class MySSHInfo {
		Session session;
		InputStream in ;
		PrintStream out;
		/**
		 * @return the session
		 */
		public Session getSession() {
			return session;
		}
		/**
		 * @param session the session to set
		 */
		public void setSession(Session session) {
			this.session = session;
		}
		/**
		 * @return the in
		 */
		public InputStream getIn() {
			return in;
		}
		/**
		 * @param in the in to set
		 */
		public void setIn(InputStream in) {
			this.in = in;
		}
		/**
		 * @return the out
		 */
		public PrintStream getOut() {
			return out;
		}
		/**
		 * @param out the out to set
		 */
		public void setOut(PrintStream out) {
			this.out = out;
		}

	}

	/**
	 *
	 * @return MySSHInfo class
	 */
	public MySSHInfo getMySSHInfo ()
	   {
	      return new MySSHInfo ();
	   }

	public static void main(String agrs[]){
		try {
			//ssh = new SSHTools(server,"colt123","colt123");
			SSHTools ssh = new SSHTools("10.91.141.78","colt123","colt123");
			//SSHTools.MySSHInfo sshInfoGet = ssh.getMySSHInfo();
			//sshInfoGet = ssh.sshInfo();
			ssh.executeShell("display arp");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
