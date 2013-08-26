/*
 * Copyright (c) 2012 Spout LLC <http://www.spout.org>
 * All Rights Reserved, unless otherwise granted permission.
 *
 * You may use and modify for private use, fork the official repository
 * for contribution purposes, contribute code, and reuse your own code.
 */
package org.spout.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.ArrayUtils;

public final class SSLUtils {
	private SSLUtils() {
	}

	public static void installCertificates() {
		try {
			// StartSSL certs
			final KeyStore trustKeyStore = KeyStore.getInstance("JKS");
			trustKeyStore.load(SSLUtils.class.getResourceAsStream("/org/spout/platform/resources/ssl/starsslcrts"), "changeit".toCharArray());
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(trustKeyStore);
			final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

			// Default java certs
			String defaultFile = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
			final KeyStore trustKeyStoreJavaStandard = KeyStore.getInstance("JKS");
			trustKeyStoreJavaStandard.load(new FileInputStream(defaultFile), "changeit".toCharArray());
			final TrustManagerFactory trustManagerFactoryJavaStandard = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactoryJavaStandard.init(trustKeyStore);
			final TrustManager[] trustManagersJavaStandard = trustManagerFactoryJavaStandard.getTrustManagers();

			TrustManager[] allTrustManagers = (TrustManager[]) ArrayUtils.addAll(trustManagers, trustManagersJavaStandard);

			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, allTrustManagers, null);
			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (final IOException | KeyStoreException | CertificateException | KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Error handling
			e.printStackTrace();
		}
	}
}
