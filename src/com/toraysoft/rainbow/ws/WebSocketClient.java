package com.toraysoft.rainbow.ws;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.BasicNameValuePair;

import android.widget.SlidingDrawer;

import com.toraysoft.rainbow.listener.WebSocketClientListener;
import com.toraysoft.rainbow.util.Base64;
import com.toraysoft.rainbow.util.LogUtil;
import com.toraysoft.rainbow.util.TextUtils;

public class WebSocketClient {
	private static final String TAG = "WebSocketClient";

	private URI mURI;
	private WebSocketClientListener mListener;
	private Socket mSocket;
	private Thread mThread;
	private List<BasicNameValuePair> mExtraHeaders;
	private HybiParser mParser;

	private final Object mSendLock = new Object();

	private static TrustManager[] sTrustManagers;

	public static void setTrustManagers(TrustManager[] tm) {
		sTrustManagers = tm;
	}

	public WebSocketClient(URI uri, WebSocketClientListener listener,
			List<BasicNameValuePair> extraHeaders) {
		mURI = uri;
		mListener = listener;
		mExtraHeaders = extraHeaders;
		mParser = new HybiParser(this);

	}

	public WebSocketClientListener getListener() {
		return mListener;
	}

	public void setExtraHeaders(List<BasicNameValuePair> extraHeaders) {
		this.mExtraHeaders = extraHeaders;
	}

	public void connect() {
		if (mThread != null && mThread.isAlive()) {
			LogUtil.d(TAG, "==========connect thread != null && mThread.isAlive==========");
			return;
		}

		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String secret = createSecret();

					int port = (mURI.getPort() != -1) ? mURI.getPort() : (mURI
							.getScheme().equals("wss") ? 443 : 80);

					String path = TextUtils.isEmpty(mURI.getPath()) ? "/"
							: mURI.getPath();
					if (!TextUtils.isEmpty(mURI.getQuery())) {
						path += "?" + mURI.getQuery();
					}

					String originScheme = mURI.getScheme().equals("wss") ? "https"
							: "http";
					URI origin = new URI(originScheme, "//" + mURI.getHost(),
							null);

					SocketFactory factory = mURI.getScheme().equals("wss") ? getSSLSocketFactory()
							: SocketFactory.getDefault();

					mSocket = factory.createSocket(mURI.getHost(), port);

					PrintWriter out = new PrintWriter(mSocket.getOutputStream());
					out.print("GET " + path + " HTTP/1.1\r\n");
					out.print("Upgrade: websocket\r\n");
					out.print("Connection: Upgrade\r\n");
					out.print("Host: " + mURI.getHost() + "\r\n");
					out.print("Origin: " + origin.toString() + "\r\n");
					out.print("Sec-WebSocket-Key: " + secret + "\r\n");
					out.print("Sec-WebSocket-Version: 13\r\n");
					if (mExtraHeaders != null) {
						for (NameValuePair pair : mExtraHeaders) {
//							LogUtil.d("" + pair.getName(), "" + pair.getValue());
							out.print(String.format("%s: %s\r\n",
									pair.getName(), pair.getValue()));
						}
					}
					out.print("\r\n");
					out.flush();

					HybiParser.HappyDataInputStream stream = new HybiParser.HappyDataInputStream(
							mSocket.getInputStream());
					// Read HTTP response status line.
					StatusLine statusLine = parseStatusLine(readLine(stream));
					if (statusLine == null) {
						throw new HttpException(
								"Received no reply from server.");
					} else if (statusLine.getStatusCode() != HttpStatus.SC_SWITCHING_PROTOCOLS) {
						throw new HttpResponseException(statusLine
								.getStatusCode(), statusLine.getReasonPhrase());
					}

					// Read HTTP response headers.
					String line;
					boolean validated = false;

					while (!TextUtils.isEmpty(line = readLine(stream))) {
						Header header = parseHeader(line);
						if (header.getName().equals("Sec-WebSocket-Accept")) {
							String expected = createSecretValidation(secret);
							String actual = header.getValue().trim();

							if (!expected.equals(actual)) {
								throw new HttpException(
										"Bad Sec-WebSocket-Accept header value.");
							}

							validated = true;
						}
					}

					if (!validated) {
						throw new HttpException(
								"No Sec-WebSocket-Accept header.");
					}

					mListener.onConnect();

					// Now decode websocket frames.
					mParser.start(stream);

				} catch (EOFException ex) {
					LogUtil.d(TAG, "WebSocket EOF!");
					mListener.onDisconnect(0, "EOF");

				} catch (SSLException ex) {
					// Connection reset by peer
					LogUtil.d(TAG, "Websocket SSL error!");
					mListener.onDisconnect(0, "SSL");

				} catch (Exception ex) {
					LogUtil.d(TAG, "Websocket error!");
					mListener.onError(ex);
				}
			}
		});
		mThread.start();
	}

	public void disconnect() {
		if (mSocket != null) {
			try {
				mSocket.close();
				mSocket = null;
			} catch (IOException ex) {
				LogUtil.d(TAG, "Error while disconnecting");
				mListener.onError(ex);
			}
		}
	}

	public boolean isConnected() {
		return (mSocket != null && mSocket.isConnected());
	}

	public void send(String data) {
		sendFrame(mParser.frame(data));
	}

	public void send(byte[] data) {
		sendFrame(mParser.frame(data));
	}

	private StatusLine parseStatusLine(String line) {
		if (TextUtils.isEmpty(line)) {
			return null;
		}
		return BasicLineParser.parseStatusLine(line, new BasicLineParser());
	}

	private Header parseHeader(String line) {
		return BasicLineParser.parseHeader(line, new BasicLineParser());
	}

	// Can't use BufferedReader because it buffers past the HTTP data.
	private String readLine(HybiParser.HappyDataInputStream reader)
			throws IOException {
		int readChar = reader.read();
		if (readChar == -1) {
			return null;
		}
		StringBuilder string = new StringBuilder("");
		while (readChar != '\n') {
			if (readChar != '\r') {
				string.append((char) readChar);
			}

			readChar = reader.read();
			if (readChar == -1) {
				return null;
			}
		}
		return string.toString();
	}

	private String createSecret() {
		byte[] nonce = new byte[16];
		for (int i = 0; i < 16; i++) {
			nonce[i] = (byte) (Math.random() * 256);
		}
		try {
			return new String(Base64.encode(nonce), "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createSecretValidation(String secret) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update((secret + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
					.getBytes());
			try {
				return new String(Base64.encode(md.digest()), "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	void sendFrame(final byte[] frame) {
		try {
			synchronized (mSendLock) {
				if (mSocket == null) {
					LogUtil.d(TAG, "Socket not connected");
					return;
				}
				OutputStream outputStream = mSocket.getOutputStream();
				outputStream.write(frame);
				outputStream.flush();
			}
		} catch (IOException e) {
			mListener.onError(e);
		}
	}

	private SSLSocketFactory getSSLSocketFactory()
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, sTrustManagers, null);
		return context.getSocketFactory();
	}

	public void ping() {
		if (mParser != null) {
			mParser.ping("".getBytes());
		}
	}

}