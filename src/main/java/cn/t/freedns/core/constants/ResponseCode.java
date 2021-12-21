package cn.t.freedns.core.constants;

/**
 * ResponseCode
 *
 * @author <a href="mailto:spider-warrior@liby.ltd">研发部-蜘蛛大侠</a>
 * @version V1.0
 * @since 2021-12-20 18:55
 **/
public enum ResponseCode {

    /**
     * No error condition
     */
    NO_ERROR((byte)0),

    /**
     * Format error
     * The name server was unable to interpret the query.
     */
    FORMAT_ERROR((byte)1),

    /**
     * Server failure
     * The name server was  unable to process this query due to a problem with the name server.
     */
    SERVER_FAILURE((byte)2),

    /**
     * Name Error
     * Meaningful only for responses from an authoritative name server,
     * this code signifies that the domain name referenced in the query does not exist.
     */
    NAME_ERROR((byte)3),

    /**
     * Not Implemented
     * The name server does not support the requested kind of query
     */
    NOT_IMPLEMENTED((byte)4),

    /**
     * Refused
     * The name server refuses to  perform the specified operation for policy
     * reasons.  For example, a name server may not wish to provide the
     * information to the particular requester, or a name server may not wish
     * to perform a particular operation
     */
    REFUSED((byte)5),

    ;

    public final byte value;

    ResponseCode(byte value) {
        this.value = value;
    }
}
