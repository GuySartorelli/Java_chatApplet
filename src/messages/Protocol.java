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
    public static final String SERVER = "Server";
    public static final String WELCOME = "WLCM";
    public static final String MESSAGE = "MSG";
    public static final String ACTION = "ACTN";
    public static final String USER_ENTER = "ENTER";
    public static final String USER_EXIT = "EXIT";

    /*
     * The protocol is to be sent/received as follows:
     * WELCOME +DELIM+ name +DELIM+ msg +DELIM+ user +DELIM+ user +DELIM+ useretc
     * MESSAGE +DELIM+ from +DELIM+ to +DELIM+ msg //maybe rgb to be added later?
     * ACTION +DELIM+ from +DELIM+ to +DELIM+ actn //note here "to" is a user for private messaging or SERVER
     * USER_ENTER +DELIM+ user
     * USER_EXIT +DELIM+ user
     */
}
