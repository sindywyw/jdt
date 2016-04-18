# Introduction #
The jdt project is an open source project that enables other developers to build application that require a fast, reliable and open source Delaunay-Triangulation implementation.


# Details #
Tje jdt project is written in Java, using the eclipse IDE.
There are currently source archives waiting for you to download in the **Downloads** section of the project's page.

The project source tree looks like this:
![http://img14.imageshack.us/img14/3695/jdt.png](http://img14.imageshack.us/img14/3695/jdt.png)

This may change, however, in the future.

# The delaunay\_triangulation package #
The main portion of the project is the **delaunay\_triangualtion package**, which actually implements the logic of the Delaunay Triangulation.

# Other packages #
The other packages are the **algorithms package** the **gui package** and the **test package**.

The **algorithms package** implements logic regarding various algorithms, such as **line of sight** and **topographic map**.

The **gui package** contains a JFrame example that uses the delaunay\_triangulation package in order to draw it on the screen, and enable the user to add points to the trianguation spontaneously. This frame demonstrates the current functionality of the jdt project.
The code to this example application is fully supplied and can be used to see how the jdt various classes can be used together.

The **test package** contains various tests that are run of the project's classes in order to test them.

# Input and Output Formats #
The two currently available formats for the example demonstration application described above are **.tsin** and **.smf**. Example files can be seen in the **data folder** of the available source download.

# Documentation #
A full Javadoc documentation is supplied in the "docs folder" of the available source download.

# What Awaits in the Future #
C# implementation<br>
Real time triangulation<br>
Line of sight<br>
Topographic map<br>
Deleting a point from the Delaunay Triangulation<br>
Etc...