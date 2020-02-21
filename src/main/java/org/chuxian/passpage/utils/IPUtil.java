package org.chuxian.passpage.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cn.hutool.core.net.NetUtil;
import cn.hutool.log.LogFactory;

public final class IPUtil {

	public static String getServerAddr() {
		return getServerAddrOpt();
	}

	public static String getServerAddrByJDK() {
		String addr = "localhost";
		try {
			addr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LogFactory.get().error(e);
		} // 获得本机IP
		return addr;
	}

	public static String getServerAddrByHutool() {
		return NetUtil.getLocalhostStr();
	}

	public static String getServerAddrOpt() {
		final List<String> addrs = getServerAddrByMultiNetCard();
		return addrs.get(addrs.size()-1);
	}
	
	public static List<String> getServerAddrByMultiNetCard() {
		final List<String> addrs = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = null;
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresss = ni.getInetAddresses();
				while (addresss.hasMoreElements()) {
					InetAddress nextElement = addresss.nextElement();
					String hostAddress = nextElement.getHostAddress();
					System.out.println("本机IP地址为：" + hostAddress);
					if(!hostAddress.contains("127.0.0.1") && !hostAddress.contains("0:0:0:0:0:0:0:1") && hostAddress.length()<=15) {
						addrs.add(hostAddress);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogFactory.get().error(e);
		}
		return addrs;
	}

}
