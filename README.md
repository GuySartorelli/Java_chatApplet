This started off as a school assignment to learn basic networking and multi-threaded applications in Java. I took it a little bit further by adding additional features.

# How to use it:
Run `ChatServer.java` once to have a server listening (by default this will listen on port `9090` but you can change that in the `ChatServer.java` file.
Each user should run `ChatGUI.java` to connect to the server. The Default IP address for this is `127.0.0.1` so if running the client from a computer other than the one running the server, this will need to be changed in `UserGUI.java`.

You can broadcast to all logged in users by typing a message in the Server tab. Left clicking on a user in the users pane on the right will open a private chat window, which allows you to communicate privately with that user.

### Note: logging in may not work outside of the VUW local network. You may need to set up your own SQL server with a `users` table with the appropriate columns as per `ChatDB.java`'s requirements.



##### Things I had on my todo list:

Consider limiting the number of characters in the textfield

bonus: options to ignore (no private) or mute (nothing in main chat either) a user on right click

bonus: press up arrow in textfield to get previous message (only store one prv message at a time)

bonus: when signing up for the first time, automatically outputs a help message. If logging in after that,
	has "type /help for the help message" appended to welcome message. 
	
bonus: added a screen for IP and PORT configuration from client instead of hardcoded.
