package cn.t.freedns.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * MemoryIpMappingRepositoryImpl
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 10:50
 **/
public class MemoryIpMappingRepositoryImpl implements IpMappingRepository {

    private static final Logger logger = LoggerFactory.getLogger(MemoryIpMappingRepositoryImpl.class);

    private final Properties properties;

    @Override
    public String getIpv4ByDomainName(String domain) {
        return properties.getProperty(domain);
    }

    public MemoryIpMappingRepositoryImpl() {
        properties = tryIpv4DomainMappingConfiguration();
    }

    private static Properties tryIpv4DomainMappingConfiguration() {
        Properties properties = new Properties();
        try (
                InputStream is = MemoryIpMappingRepositoryImpl.class.getResourceAsStream("/ipv4-domain-mapping.properties")
        ) {
            if(is == null) {
                logger.error("ipv4配置文件未找到: {}", "ipv4-domain-mapping.properties");
            } else {
                properties.load(is);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        return properties;
    }
}