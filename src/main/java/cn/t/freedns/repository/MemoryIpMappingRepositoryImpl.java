package cn.t.freedns.repository;

import cn.t.freedns.core.constants.RecordClass;
import cn.t.freedns.core.constants.RecordType;
import cn.t.freedns.core.data.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MemoryIpMappingRepositoryImpl
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 10:50
 **/
public class MemoryIpMappingRepositoryImpl implements IpMappingRepository {

    private static final Logger logger = LoggerFactory.getLogger(MemoryIpMappingRepositoryImpl.class);

    private static final Map<String, List<Record>> domainRecordListMap = new ConcurrentHashMap<>();

    @Override
    public List<Record> getIpv4RecordListByDomainName(String domain) {
        return domainRecordListMap.get(domain);
    }

    public MemoryIpMappingRepositoryImpl() {
        Properties properties = tryIpv4DomainMappingConfiguration();
        Set<String> domainSet = properties.stringPropertyNames();
        for (String domain : domainSet) {
            String ip = properties.getProperty(domain);
            if(ip == null || ip.trim().length() == 0) {
                domainRecordListMap.put(domain, Collections.emptyList());
            } else {
                String[] ipElements = ip.split("\\.");
                byte[] ipBytes = new byte[ipElements.length];
                for (int i = 0; i < ipElements.length; i++) {
                    ipBytes[i] = (byte)Short.parseShort(ipElements[i]);
                }
                Record record = new Record();
                record.setDomain(domain);
                record.setRecordType(RecordType.A.value);
                record.setRecordClass(RecordClass.IN.value);
                record.setTtl(600);
                record.setData(ipBytes);
                domainRecordListMap.put(domain, Collections.singletonList(record));
            }
        }
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