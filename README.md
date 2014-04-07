RPS
===

My Android RPS Application that integrates with my MMORPS 
software.  You can play with the software at

    https://lti-tools.dr-chuck.com/mmorps/

This is relatively simple application.   You login with your
Google account and can play Rock/Paper/Scissors with other.
If you want to pair your MMORPS account with a mobile device,
after you lok in - go to the drop-down by your name and select
"Pair with Mofile Device".  You can set or clear the pairing code.
This code will need to be typed into youy Android application.

Installing The Appication
-------------------------

A functioning install of git is a pre-requisite to this procedure.

Generally the easiest way to get this application into your Android
Studio is to get to the screen labelled "Welcome to Android Studio". 
This screen comes up either when you open Android Studio for the 
first time or when you do a "File -> Close Project" on all open projects.

Select "Check out from Version Control" in that screen and choose "git".

In the dialog box that comes up - use this as the Git Repository URL:

    https://github.com/csev/RPS.git

This should check out the source code and open the project in one step.
If this is the very first time you have started Android Studio you may not
have the AndroidStudioProjects folder in your home directory.  You might 
need to create this by hand.

    /Users/csev/AndroidStudioProjects

Once the checkout completes you should open the project.   You might see 
a very empty grey screen with words like "Open Project View with %1".  This
happens becuase I don't check the .idea/workspace.xml file into git.  
But don't worry - just do what it says to open the Project View and then start 
opening the files and soon Android Studio will look like it should.

How the Application Works
-------------------------

You can point this to any server you like - the default is my server.  Note
that if you want to point it to a server on your laptop, don't use the URL
with localhost.   On the Android, localhost loops back to the android 
emulator itself.  So you need to use a URL like these:

    http://192.168.1.102/mmorps/
    http://192.168.1.102:8888/mmorps/


You can use the idconfig command on Mac (and I think on Windows) to find your
IP address of your workstation.

Once you have the server URL, you enter the Pairing code and start playing.  
There is extensive logging so you can watch what is going on in the console.

As always, this is early days and comments are welcome.

/Chuck
Mon Apr  7 17:03:03 EDT 2014
