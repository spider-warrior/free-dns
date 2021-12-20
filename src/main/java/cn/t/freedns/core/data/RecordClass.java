package cn.t.freedns.core.data;

/**
 * @author yj
 * @since 2019-12-31 21:45
 **/
public enum RecordClass {

    IN((short)1, "IN", "指互联网地址"),
    CS((short)2, "CS", "the CSNET class (Obsolete - used only for examples in some obsolete RFCs)"),
    CH((short)3, "CH", "the CHAOS class"),
    HS((short)4, "HS", "Hesiod [Dyer 87]"),
    ANY((short)255, "*", "any class");

    ;
    public final short value;
    public final String shortName;
    public final String desc;

    RecordClass(short value, String shortName, String desc) {
        this.value = value;
        this.shortName = shortName;
        this.desc = desc;
    }

    public static RecordClass getRecordClass(short value) {
        for(RecordClass recordClass : values()) {
            if(recordClass.value == value) {
                return recordClass;
            }
        }
        return null;
    }
}
