package cn.t.freedns;

import cn.t.freedns.core.MessageContext;
import cn.t.freedns.core.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-17 14:51
 **/
public class DomainNameServer {

    private static final Logger logger = LoggerFactory.getLogger(DomainNameServer.class);

    public static void main(String[] args) throws IOException {
        loadSpecificDnsProperty();
        MessageHandler messageHandler = new MessageHandler();
        DatagramSocket socket = new DatagramSocket(53);
        final byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            byte[] messageBytes = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, messageBytes, 0, packet.getLength());
            MessageContext messageContext = new MessageContext();
            messageContext.setSocket(socket);
            messageContext.setInetAddress(packet.getAddress());
            messageContext.setPort(packet.getPort());

            InetAddress inetAddress = packet.getAddress();
            int port = packet.getPort();
            logger.info("message from: sourceIpAddr: {}:{}", inetAddress, port);
            messageHandler.handle(messageBytes, messageContext);
        }
    }
    private static void loadSpecificDnsProperty() {
        //sun.net.spi.nameservice.provider.<n>=<default|dns,sun|...> 用于设置域名服务提供者
        //default的时候调用系统自带的DNS
        //dns,sun的时候，会调用sun.net.spi.nameservice.nameservers=<server1_ipaddr,server2_ipaddr ...>指定的DNS来解析
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        System.setProperty("sun.net.spi.nameservice.nameservers", "192.168.1.1");
    }
}