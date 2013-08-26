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
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.spout.platform.security.CompositeX509TrustManager;

public final class SSLUtils {
	private SSLUtils() {
	}

	public static void installCertificates() {
		try {
			// StartSSL certs
			final KeyStore trustKeyStore = KeyStore.getInstance("JKS");
			trustKeyStore.load(SSLUtils.class.getResourceAsStream("/org/spout/platform/resources/ssl/starsslcrts"), "changeit".toCharArray());
			final X509TrustManager trustManagerFactory = getTrustManager("SunX509", trustKeyStore);

			// Default java certs
			String defaultFile = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
			final KeyStore trustKeyStoreJavaStandard = KeyStore.getInstance("JKS");
			trustKeyStoreJavaStandard.load(new FileInputStream(defaultFile), "changeit".toCharArray());
			final X509TrustManager trustManagerFactoryJavaStandard = getTrustManager("SunX509", trustKeyStoreJavaStandard);

			List<X509TrustManager> trustManagers = new ArrayList<>();
			trustManagers.add(trustManagerFactory);
			trustManagers.add(trustManagerFactoryJavaStandard);

			CompositeX509TrustManager compositeX509TrustManager = new CompositeX509TrustManager(trustManagers);

			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] {compositeX509TrustManager}, null);
			javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (final IOException | KeyStoreException | CertificateException | KeyManagementException | NoSuchAlgorithmException e) {
			// TODO Error handling
			e.printStackTrace();
		}
	}

	private static X509TrustManager getTrustManager(String algorithm, KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
		TrustManagerFactory factory = TrustManagerFactory.getInstance(algorithm);
		factory.init(keystore);
		return (X509TrustManager) factory.getTrustManagers()[0];
	}
}
