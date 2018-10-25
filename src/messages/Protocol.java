package messages;

public final class Protocol {
    public static final String DELIM = ":::";
    
    //server/client connection and authorisation protocol
    public static final String SUCCESS = "1";
    public static final String FAIL = "0";
    public static final String ERROR = "-1";
    public static final String SIGNUP = "SIGNUP";
    public static final String LOGIN = "LOGIN";
    public static final String CHECK_NAME = "CHKNM";
    public static final String ACKNOWLEDGE_FAIL = "FAIL";
    
    //server/client messaging protocol
    public static final String MESSAGE = "MSG";
    public static final String SERVER = "Server";
    public static final String USER_ENTER = "ENTER";
    public static final String USER_EXIT = "EXIT";
    public static final String ACTION = "ACTN";
    public static final String WELCOME = "WLCM";
    
    //msg:::from:::to:::msg //maybe rgb
    //actn:::from:::to:::actn
    //enter:::user
    //exit:::user
    //wlcm:::name:::msg:::user:::user:::useretc
}
