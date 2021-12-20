package cn.t.freedns.repository;

/**
 * IpMappingRepository
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 10:49
 **/
public interface IpMappingRepository {
    String getIpv4ByDomainName(String domain);
}