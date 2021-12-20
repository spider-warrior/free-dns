package cn.t.freedns.core.data;

/**
 * Operation
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 18:55
*  0               a standard query (QUERY)
*  1               an inverse query (IQUERY)
*  2               a server status request (STATUS)
 * 3-15            reserved for future use
 **/
public enum Operation {

    /**
     * standard query
     */
    QUERY((byte)0),

    /**
     * inverse query
     */
    INVERSE_QUERY((byte)1),

    /**
     * a server status request
     */
    STATUS((byte)2),

    ;

    public final byte value;

    Operation(byte value) {
        this.value = value;
    }
}