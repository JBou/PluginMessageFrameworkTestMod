package pmf;

import com.ikeirnez.pluginmessageframework.packet.StandardPacket;

/**
 * @author JBou
 */
public class MyPacket extends StandardPacket {

    private static final long serialVersionUID = 4714156896979723677L;

    private String message;

    public MyPacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
